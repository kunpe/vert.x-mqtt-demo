package dev.mqtt.handler;

import dev.mqtt.MqttLauncher;
import dev.mqtt.utils.MqttSessionUtils;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.mqtt.MqttEndpoint;

public class EndPointConnentHandler {
  private static final Logger log = LoggerFactory.getLogger(EndPointConnentHandler.class);

  public static void disconnect(String clientId,MqttEndpoint endpoint) {
    endpoint.disconnectHandler( event -> {
      MqttSessionUtils.remove(clientId, MqttLauncher.vertx);
      log.info(clientId + " has offline.");
    });
  }

  public static void ping(String clientId,MqttEndpoint endpoint) {
    // Be notified by client keep alive
    endpoint.pingHandler(v -> {
      System.out.println("Ping received from client,Id: " + clientId);
    });
  }
}
