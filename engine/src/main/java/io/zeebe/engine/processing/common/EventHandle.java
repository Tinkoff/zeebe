/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH under
 * one or more contributor license agreements. See the NOTICE file distributed
 * with this work for additional information regarding copyright ownership.
 * Licensed under the Zeebe Community License 1.1. You may not use this file
 * except in compliance with the Zeebe Community License 1.1.
 */
package io.zeebe.engine.processing.common;

import io.zeebe.engine.processing.deployment.model.element.ExecutableCatchEvent;
import io.zeebe.engine.processing.deployment.model.element.ExecutableFlowElement;
import io.zeebe.engine.processing.deployment.model.element.ExecutableStartEvent;
import io.zeebe.engine.processing.streamprocessor.MigratedStreamProcessors;
import io.zeebe.engine.processing.streamprocessor.writers.StateWriter;
import io.zeebe.engine.processing.streamprocessor.writers.TypedCommandWriter;
import io.zeebe.engine.processing.streamprocessor.writers.Writers;
import io.zeebe.engine.state.KeyGenerator;
import io.zeebe.engine.state.immutable.EventScopeInstanceState;
import io.zeebe.engine.state.immutable.ProcessState;
import io.zeebe.engine.state.instance.ElementInstance;
import io.zeebe.protocol.impl.record.value.message.MessageRecord;
import io.zeebe.protocol.impl.record.value.message.MessageStartEventSubscriptionRecord;
import io.zeebe.protocol.impl.record.value.processinstance.ProcessEventRecord;
import io.zeebe.protocol.impl.record.value.processinstance.ProcessInstanceRecord;
import io.zeebe.protocol.record.intent.MessageStartEventSubscriptionIntent;
import io.zeebe.protocol.record.intent.ProcessEventIntent;
import io.zeebe.protocol.record.intent.ProcessInstanceIntent;
import io.zeebe.protocol.record.value.BpmnElementType;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

public final class EventHandle {

  private static final DirectBuffer NO_VARIABLES = new UnsafeBuffer();

  private final ProcessInstanceRecord recordForPICreation = new ProcessInstanceRecord();
  private final ProcessEventRecord processEventRecord = new ProcessEventRecord();
  private final MessageStartEventSubscriptionRecord startEventSubscriptionRecord =
      new MessageStartEventSubscriptionRecord();

  private final KeyGenerator keyGenerator;
  private final EventScopeInstanceState eventScopeInstanceState;
  private final ProcessState processState;

  private final TypedCommandWriter commandWriter;
  private final StateWriter stateWriter;
  private final EventTriggerBehavior eventTriggerBehavior;

  public EventHandle(
      final KeyGenerator keyGenerator,
      final EventScopeInstanceState eventScopeInstanceState,
      final Writers writers,
      final ProcessState processState,
      final EventTriggerBehavior eventTriggerBehavior) {
    this.keyGenerator = keyGenerator;
    this.eventScopeInstanceState = eventScopeInstanceState;
    this.processState = processState;
    commandWriter = writers.command();
    stateWriter = writers.state();
    this.eventTriggerBehavior = eventTriggerBehavior;
  }

  public boolean canTriggerElement(final ElementInstance eventScopeInstance) {
    return eventScopeInstance != null
        && eventScopeInstance.isActive()
        && eventScopeInstanceState.isAcceptingEvent(eventScopeInstance.getKey());
  }

  /**
   * Triggers a process by updating the state with a new {@link ProcessEventIntent#TRIGGERED} event.
   *
   * <p>NOTE: this method assumes that the caller already verified that the target can accept new
   * events!
   *
   * @param eventScope the event's scope, whose key is used to index the trigger in {@link
   *     io.zeebe.engine.state.immutable.EventScopeInstanceState}
   * @param catchEventId the ID of the element which should be triggered by the event
   * @param variables the variables/payload of the event (can be empty)
   */
  public void triggerProcessEvent(
      final ElementInstance eventScope,
      final DirectBuffer catchEventId,
      final DirectBuffer variables) {
    final var newElementInstanceKey = keyGenerator.nextKey();
    processEventRecord.reset();
    processEventRecord
        .setScopeKey(eventScope.getKey())
        .setTargetElementIdBuffer(catchEventId)
        .setVariablesBuffer(variables)
        .setProcessDefinitionKey(eventScope.getValue().getProcessDefinitionKey())
        .setProcessInstanceKey(eventScope.getValue().getProcessInstanceKey());
    stateWriter.appendFollowUpEvent(
        newElementInstanceKey, ProcessEventIntent.TRIGGERED, processEventRecord);
  }

  public void activateElement(
      final ExecutableFlowElement catchEvent,
      final long eventScopeKey,
      final ProcessInstanceRecord elementRecord) {
    activateElement(catchEvent, eventScopeKey, elementRecord, NO_VARIABLES);
  }

  public void activateElement(
      final ExecutableFlowElement catchEvent,
      final long eventScopeKey,
      final ProcessInstanceRecord elementRecord,
      final DirectBuffer variables) {

    if (MigratedStreamProcessors.isMigrated(elementRecord.getBpmnElementType())) {

      if (isElementActivated(catchEvent)) {
        commandWriter.appendFollowUpCommand(
            eventScopeKey, ProcessInstanceIntent.COMPLETE_ELEMENT, elementRecord);
      } else if (catchEvent.getFlowScope().getElementType() == BpmnElementType.EVENT_SUB_PROCESS) {
        final var executableStartEvent = (ExecutableStartEvent) catchEvent;

        eventTriggerBehavior.triggerEventSubProcess(
            executableStartEvent, eventScopeKey, elementRecord);
      } else if (isInterrupting(catchEvent)) {
        // terminate the activated element and then activate the triggered catch event
        commandWriter.appendFollowUpCommand(
            eventScopeKey, ProcessInstanceIntent.TERMINATE_ELEMENT, elementRecord);

      } else {
        eventTriggerBehavior.activateTriggeredEvent(
            catchEvent, elementRecord.getFlowScopeKey(), elementRecord, variables);
      }
    }
  }

  private boolean isElementActivated(final ExecutableFlowElement catchEvent) {
    return catchEvent.getElementType() == BpmnElementType.INTERMEDIATE_CATCH_EVENT
        || catchEvent.getElementType() == BpmnElementType.RECEIVE_TASK
        || catchEvent.getElementType() == BpmnElementType.EVENT_BASED_GATEWAY;
  }

  private boolean isInterrupting(final ExecutableFlowElement catchEvent) {
    if (catchEvent instanceof ExecutableCatchEvent) {
      final var event = (ExecutableCatchEvent) catchEvent;
      return event.isInterrupting();
    } else {
      return false;
    }
  }

  public void triggerMessageStartEvent(
      final long processDefinitionKey,
      final DirectBuffer startEventElementId,
      final long messageKey,
      final MessageRecord message) {

    final var newProcessInstanceKey = keyGenerator.nextKey();
    final var bpmnProcessId = processState.getProcessByKey(processDefinitionKey).getBpmnProcessId();

    startEventSubscriptionRecord
        .setProcessDefinitionKey(processDefinitionKey)
        .setBpmnProcessId(bpmnProcessId)
        .setStartEventId(startEventElementId)
        .setProcessInstanceKey(newProcessInstanceKey)
        .setCorrelationKey(message.getCorrelationKeyBuffer())
        .setMessageKey(messageKey)
        .setMessageName(message.getNameBuffer())
        .setVariables(message.getVariablesBuffer());

    // TODO (saig0): the subscription should have a key (#2805)
    stateWriter.appendFollowUpEvent(
        -1L, MessageStartEventSubscriptionIntent.CORRELATED, startEventSubscriptionRecord);

    activateProcessInstanceForStartEvent(processDefinitionKey, newProcessInstanceKey);
  }

  public void activateProcessInstanceForStartEvent(
      final long processDefinitionKey, final long processInstanceKey) {

    final var process = processState.getProcessByKey(processDefinitionKey);

    recordForPICreation
        .setBpmnProcessId(process.getBpmnProcessId())
        .setProcessDefinitionKey(process.getKey())
        .setVersion(process.getVersion())
        .setProcessInstanceKey(processInstanceKey)
        .setElementId(process.getProcess().getId())
        .setBpmnElementType(process.getProcess().getElementType());

    commandWriter.appendFollowUpCommand(
        processInstanceKey, ProcessInstanceIntent.ACTIVATE_ELEMENT, recordForPICreation);
  }
}
