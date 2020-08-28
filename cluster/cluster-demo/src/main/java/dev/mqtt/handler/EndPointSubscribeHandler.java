package dev.mqtt.handler;

import dev.mqtt.MqttLauncher;
import dev.mqtt.utils.MqttSessionUtils;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.MqttTopicSubscription;

import java.util.List;

public class EndPointSubscribeHandler {
  private static final Logger log = LoggerFactory.getLogger(EndPointSubscribeHandler.class);


  public static void subscribeHandler(String clientId,MqttEndpoint endpoint,List<MqttQoS> grantedQosLevels) {
    endpoint.subscribeHandler(subscribe -> {
      for (MqttTopicSubscription s: subscribe.topicSubscriptions()) {
        log.info("Subscription for " + s.topicName() + " with QoS " + s.qualityOfService());
        grantedQosLevels.add(s.qualityOfService());
        // 添加到缓存
        MqttSessionUtils.updateSubscription(clientId,s,MqttLauncher.vertx);
      }
      endpoint.subscribeAcknowledge(subscribe.messageId(), grantedQosLevels);
    });
  }

  public static void unSubscribeHandler(String clientId,MqttEndpoint endpoint,List<MqttQoS> grantedQosLevels) {

    endpoint.unsubscribeHandler(unsubscribe -> {

      for (String t: unsubscribe.topics()) {
        log.info("Unsubscription for " + t);

        MqttSessionUtils.removeSubscription(clientId, t,MqttLauncher.vertx);
      }
      endpoint.unsubscribeAcknowledge(unsubscribe.messageId());
    });
  }

  }
