package dev.mqtt.container;

import dev.mqtt.entity.MqttSession;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.MqttTopicSubscription;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mqtt 客服端保存容器
 * @author kun
 * @version 1.0
 * @Date 2020/8/27
 */
public class MqttSessionContainer extends ConcurrentHashMap<String, MqttSession> {
  public static MqttSessionContainer mqttSessionContainer = null;
  static {
    mqttSessionContainer = new MqttSessionContainer();
  }
  public static MqttSessionContainer getAndPut(String clientId, MqttTopicSubscription mqttTopicSubscription) {
    MqttSession mqttSession;
    if (!MqttSessionContainer.mqttSessionContainer.containsKey(clientId)) {
      mqttSession = new MqttSession();
    } else {
      mqttSession = mqttSessionContainer.get(clientId);
      Set<MqttTopicSubscription> mqttTopicSubscriptions = mqttSession.mqttTopicSubscriptions();
      mqttTopicSubscriptions.add(mqttTopicSubscription);
    }
    return mqttSessionContainer;
  }

  public static MqttEndpoint getByClientId(String clientId) {
    MqttSession mqttSession = mqttSessionContainer.get(clientId);
    return mqttSession.endpoint();
  }

}
