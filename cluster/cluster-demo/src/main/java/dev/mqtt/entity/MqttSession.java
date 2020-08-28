package dev.mqtt.entity;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.buffer.impl.BufferImpl;
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
public class MqttSession extends BufferImpl {
  private MqttEndpoint endpoint;
  private Set<MqttTopicSubscription> mqttTopicSubscriptions = new HashSet<>();

}
