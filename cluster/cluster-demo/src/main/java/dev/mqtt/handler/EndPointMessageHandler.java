package dev.mqtt.handler;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.mqtt.MqttEndpoint;

import java.nio.charset.Charset;
import java.util.Arrays;

public class EndPointMessageHandler {
  private static final Logger log = LoggerFactory.getLogger(EndPointMessageHandler.class);

  public static void received(MqttEndpoint endpoint) {
    endpoint.publishHandler(message -> {

      log.info("Just received message [" + message.payload().toString(Charset.defaultCharset()) + "] with QoS [" + message.qosLevel() + "]");
      endpoint.publish(message.topicName(),
        Buffer.buffer(message.payload().toString(Charset.defaultCharset())),
        MqttQoS.EXACTLY_ONCE,
        false,
        false);
      if (message.qosLevel() == MqttQoS.AT_LEAST_ONCE) {
        endpoint.publishAcknowledge(message.messageId());
      } else if (message.qosLevel() == MqttQoS.EXACTLY_ONCE) {
        endpoint.publishReceived(message.messageId());
      }
    }).publishReleaseHandler(messageId -> {
      endpoint.publishComplete(messageId);
    });
  }

  public static void publish(String topic, String message, MqttEndpoint ...endpoints) {
    Arrays.stream(endpoints).forEach(endpoint -> {
      endpoint.publish(topic,
        Buffer.buffer(message),
        MqttQoS.EXACTLY_ONCE,
        false,
        false);
      endpoint.publishAcknowledgeHandler(messageId -> {
        System.out.println("Received ack for message = " +  messageId);
      }).publishReceivedHandler(messageId -> {
        endpoint.publishRelease(messageId);
      }).publishCompletionHandler(messageId -> {
        System.out.println("Received ack for message = " +  messageId);
      });
    });
  }
}
