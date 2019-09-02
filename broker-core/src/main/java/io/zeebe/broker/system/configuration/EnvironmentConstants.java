/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH under
 * one or more contributor license agreements. See the NOTICE file distributed
 * with this work for additional information regarding copyright ownership.
 * Licensed under the Zeebe Community License 1.0. You may not use this file
 * except in compliance with the Zeebe Community License 1.0.
 */
package io.zeebe.broker.system.configuration;

public class EnvironmentConstants {

  public static final String ENV_NODE_ID = "ZEEBE_NODE_ID";
  public static final String ENV_HOST = "ZEEBE_HOST";
  public static final String ENV_PORT_OFFSET = "ZEEBE_PORT_OFFSET";
  public static final String ENV_INITIAL_CONTACT_POINTS = "ZEEBE_CONTACT_POINTS";
  public static final String ENV_DIRECTORIES = "ZEEBE_DIRECTORIES";
  public static final String ENV_PARTITIONS_COUNT = "ZEEBE_PARTITIONS_COUNT";
  public static final String ENV_REPLICATION_FACTOR = "ZEEBE_REPLICATION_FACTOR";
  public static final String ENV_CLUSTER_SIZE = "ZEEBE_CLUSTER_SIZE";
  public static final String ENV_CLUSTER_NAME = "ZEEBE_CLUSTER_NAME";
  public static final String ENV_EMBED_GATEWAY = "ZEEBE_EMBED_GATEWAY";
  public static final String ENV_DEBUG_EXPORTER = "ZEEBE_DEBUG";
  public static final String ENV_GOSSIP_BROADCAST_UPDATES =
      "ZEEBE_BROKER_CLUSTER_GOSSIPBROADCASTUPDATES";
  public static final String ENV_GOSSIP_BROADCAST_DISPUTES =
      "ZEEBE_BROKER_CLUSTER_GOSSIPBROADCASTDISPUTES";
  public static final String ENV_GOSSIP_NOTIFY_SUSPECT = "ZEEBE_BROKER_CLUSTER_NOTIFYSUSPECT";
  public static final String ENV_GOSSIP_INTERVAL = "ZEEBE_BROKER_CLUSTER_GOSSIPINTERVAL";
  public static final String ENV_GOSSIP_FANOUT = "ZEEBE_BROKER_CLUSTER_GOSSIPFANOUT";
  public static final String ENV_GOSSIP_PROBE_INTERVAL = "ZEEBE_BROKER_CLUSTER_GOSSIPPROBEINTERVAL";
  public static final String ENV_GOSSIP_SUSPECT_PROBES = "ZEEBE_BROKER_CLUSTER_SUSPECTEDPROBES";
  public static final String ENV_GOSSIP_FAILURE_TIMEOUT =
      "ZEEBE_BROKER_CLUSTER_GOSSIPFAILURETIMEOUT";
  public static final String ENV_EXECUTION_METRICS_EXPORTER_ENABLED =
      "ZEEBE_BROKER_EXECUTIONMETRICSEXPORTERENABLED";
  public static final String ENV_BACKPRESSURE_ENABLED = "ZEEBE_BROKER_BACKPRESSURE_ENABLED";
  public static final String ENV_BACKPRESSURE_ALGORITHM = "ZEEBE_BROKER_BACKPRESSURE_ALGORITHM";
  public static final String ENV_BACKPRESSURE_WINDOWED = "ZEEBE_BROKER_BACKPRESSURE_USEWINDOWED";

  public static final String ENV_BROKER_BACKPRESSURE_AIMD_BACKOFF_RATIO =
      "ZEEBE_BROKER_BACKPRESSURE_ENABLED";
  public static final String ENV_BROKER_BACKPRESSURE_AIMD_MIN_LIMIT =
      "ZEEBE_BROKER_BACKPRESSURE_AIMD_MINLIMIT";
  public static final String ENV_BROKER_BACKPRESSURE_AIMD_REQUEST_TIMEOUT =
      "ZEEBE_BROKER_BACKPRESSURE_AIMD_REQUEST_TIMEOUT";
  public static final String ENV_BROKER_BACKPRESSURE_AIMD_INITIAL_LIMIT =
      "ZEEBE_BROKER_BACKPRESSURE_AIMD_INITIALLIMIT";
  public static final String ENV_BROKER_BACKPRESSURE_AIMD_MAX_LIMIT =
      "ZEEBE_BROKER_BACKPRESSURE_AIMD_MAXLIMIT";

  public static final String ENV_BROKER_BACKPRESSURE_FIXED_LIMIT =
      "ZEEBE_BROKER_BACKPRESSURE_FIXEDLIMIT_LIMIT";

  public static final String ENV_BROKER_BACKPRESSURE_VEGAS_ALPHA = "ZEEBE_BACKPRESSURE_VEGAS_ALPHA";
  public static final String ENV_BROKER_BACKPRESSURE_VEGAS_BETA = "ZEEBE_BACKPRESSURE_VEGAS_BETA";
  public static final String ENV_BROKER_BACKPRESSURE_VEGAS_INITIALLIMIT =
      "ZEEBE_BACKPRESSURE_VEGAS_INITIALLIMIT";

  public static final String ENV_BROKER_BACKPRESSURE_GRADIENT_MINLIMIT =
      "ZEEBE_BROKER_BACKPRESSURE_GRADIENT_MINLIMIT";
  public static final String ENV_BROKER_BACKPRESSURE_GRADIENT_INITIALLIMIT =
      "ZEEBE_BROKER_BACKPRESSURE_GRADIENT_INITIALLIMIT";
  public static final String ENV_BROKER_BACKPRESSURE_GRADIENT_RTTTOLERANCE =
      "\"ZEEBE_BROKER_BACKPRESSURE_GRADIENT_RTTTOLERANCE";

  public static final String ENV_BROKER_BACKPRESSURE_GRADIENT2_MINLIMIT =
      "ZEEBE_BROKER_BACKPRESSURE_GRADIENT2_MINLIMIT";
  public static final String ENV_BROKER_BACKPRESSURE_GRADIENT2_INITIALLIMIT =
      "ZEEBE_BROKER_BACKPRESSURE_GRADIENT2_INITIALLIMIT";
  public static final String ENV_BROKER_BACKPRESSURE_GRADIENT2_RTTTOLERANCE =
      "ZEEBE_BROKER_BACKPRESSURE_GRADIENT2_RTTTOLERANCE";
  public static final String ENV_BROKER_BACKPRESSURE_GRADIENT2_LONGWINDOW =
      "ZEEBE_BROKER_BACKPRESSURE_GRADIENT2_LONGWINDOW";
}