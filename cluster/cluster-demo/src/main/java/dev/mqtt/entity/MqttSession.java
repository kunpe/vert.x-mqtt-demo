package dev.mqtt.entity;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.buffer.impl.BufferImpl;
import io.vertx.core.shareddata.Shareable;
import io.vertx.core.shareddata.impl.ClusterSerializable;
import io.vertx.ext.web.sstore.impl.SharedDataSessionImpl;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.MqttTopicSubscription;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Accessors(fluent = true)
public class MqttSession implements  Shareable {
  private MqttEndpoint endpoint;
  private Set<MqttTopicSubscription> mqttTopicSubscriptions = new HashSet<>();

}
