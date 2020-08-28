package dev.mqtt.entity;

import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.MqttTopicSubscription;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Accessors(fluent = true)
public class MqttSession {
  private MqttEndpoint endpoint;
  private Set<MqttTopicSubscription> mqttTopicSubscriptions = new HashSet<>();

}
