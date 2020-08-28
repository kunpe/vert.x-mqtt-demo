package dev.mqtt;

import dev.mqtt.entity.MqttSession;
import dev.mqtt.handler.EndPointConnentHandler;
import dev.mqtt.handler.EndPointMessageHandler;
import dev.mqtt.handler.EndPointSubscribeHandler;
import dev.mqtt.utils.MqttSessionUtils;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.mqtt.MqttServerOptions;

import java.util.ArrayList;
import java.util.List;

public class MqttServer extends AbstractVerticle {
  private static final Logger log = LoggerFactory.getLogger(MqttServer.class);

  List<MqttQoS> grantedQosLevels;


  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    io.vertx.mqtt.MqttServer mqttServer = io.vertx.mqtt.MqttServer.create(vertx);
    MqttServerOptions options = new MqttServerOptions();
    options.setTcpFastOpen(MqttServerOptions.DEFAULT_TCP_FAST_OPEN);
    mqttServer.endpointHandler(endpoint -> {
      String clientId = endpoint.clientIdentifier();
      log.info("MQTT client [" + endpoint.clientIdentifier() + "] request to connect, clean session = " + endpoint.isCleanSession());
      if (endpoint.auth() != null) {
        log.info("username = " + endpoint.auth().getUsername() + ", password = " + endpoint.auth().getPassword() + "");
      }
      log.info("keep alive timeout = " + endpoint.keepAliveTimeSeconds() + "");

      // accept connection from the remote client
      endpoint.accept(false);
      MqttSessionUtils.put(endpoint.clientIdentifier(),new MqttSession().endpoint(endpoint),vertx);


      grantedQosLevels = new ArrayList<>();
      // 注册下线事件监听
      EndPointConnentHandler.disconnect(clientId,endpoint);
      // 心跳检测
      EndPointConnentHandler.ping(clientId,endpoint);
      // 注册订阅Borker
      EndPointSubscribeHandler.subscribeHandler(clientId,endpoint,grantedQosLevels);
      // 取消注册Borker
      EndPointSubscribeHandler.unSubscribeHandler(clientId,endpoint,grantedQosLevels);
      // 监听客户端Topic
      EndPointMessageHandler.received(endpoint);
    }).listen(handler -> {
      if (handler.succeeded()) {
        log.info("Mqtt server startup.");
      } else {
        log.error("Mqtt server startup error.");
      }
    });
  }


  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    log.info("stopped server.");
  }
}
