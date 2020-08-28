package dev.imkun.mqttserver;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttServer;
import io.vertx.mqtt.MqttServerOptions;
import io.vertx.mqtt.MqttTopicSubscription;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisConnection;
import io.vertx.redis.client.RedisOptions;
import io.vertx.redis.client.impl.RedisClient;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Mqtt Server
 * @author kun
 * @version 1.0
 * @Date 2020/8/13
 *
 * @Copyright 2016-2020 重庆锋云汇智数据科技有限公司. All rights reserved. Power by 技术支持中心
 */
public class MqttServerVerticle extends AbstractVerticle {

  List<MqttQoS> grantedQosLevels;

  RedisClient client;

  RedisAPI redisAPI;
  private void init() {
    vertx.sharedData();
  }
  private void initRedis() {
    RedisOptions redisOptions = new RedisOptions().setConnectionString("redis://127.0.0.1:6379");
    Redis.createClient(vertx, redisOptions)
      .connect(onConnect -> {
        if (onConnect.succeeded()) {
          RedisConnection redisConnection = onConnect.result();
          redisAPI = RedisAPI.api(redisConnection);
        }
      });
  }

  private void initRedisClient() {
    // Create the redis client
    client = new RedisClient(vertx,
      new RedisOptions().setConnectionString("redis://127.0.0.1:6379"));
  }
  /**
   * 系统启动方法 {@link Verticle#start(io.vertx.core.Promise)}
   * @param startPromise
   * @throws Exception
   */
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    this.initRedis();
//    this.initRedisClient();
    MqttServer mqttServer = MqttServer.create(vertx);
    MqttServerOptions options = new MqttServerOptions();
    options.setTcpFastOpen(MqttServerOptions.DEFAULT_TCP_FAST_OPEN);
    mqttServer.endpointHandler(endpoint -> {

      // shows main connect info
      System.out.println("MQTT client [" + endpoint.clientIdentifier() + "] request to connect, clean session = " + endpoint.isCleanSession());

      if (endpoint.auth() != null) {
        System.out.println("[username = " + endpoint.auth().getUsername() + ", password = " + endpoint.auth().getPassword() + "]");
      }
//      if (endpoint.will() != null) {
//        System.out.println("[will topic = " + endpoint.will().getWillTopic() + " msg = " + new String(endpoint.will().getWillMessageBytes()) +
//          " QoS = " + endpoint.will().getWillQos() + " isRetain = " + endpoint.will().isWillRetain() + "]");
//      }

      System.out.println("[keep alive timeout = " + endpoint.keepAliveTimeSeconds() + "]");

      // accept connection from the remote client
      endpoint.accept(false);

      endpoint.disconnectHandler(v -> {
        System.out.println("Received disconnect from client");
      });

      // Handling client subscription/unsubscription request
      endpoint.subscribeHandler(subscribe -> {

        grantedQosLevels = new ArrayList<>();
        for (MqttTopicSubscription s: subscribe.topicSubscriptions()) {
          System.out.println("Subscription for " + s.topicName() + " with QoS " + s.qualityOfService());
          grantedQosLevels.add(s.qualityOfService());
        }
        // ack the subscriptions request
        endpoint.subscribeAcknowledge(subscribe.messageId(), grantedQosLevels);

      });

      endpoint.unsubscribeHandler(unsubscribe -> {

        for (String t: unsubscribe.topics()) {
          System.out.println("Unsubscription for " + t);
        }
        // ack the subscriptions request
        endpoint.unsubscribeAcknowledge(unsubscribe.messageId());
      });

      // Handling client published message
      endpoint.publishHandler(message -> {

        System.out.println("Just received message [" + message.payload().toString(Charset.defaultCharset()) + "] with QoS [" + message.qosLevel() + "]");
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

      // Publish message to the client
      endpoint.publish("my_topic",
        Buffer.buffer("Hello from the Vert.x MQTT server"),
        MqttQoS.EXACTLY_ONCE,
        false,
        false);
      // specifing handlers for handling QoS 1 and 2
      endpoint.publishAcknowledgeHandler(messageId -> {
        System.out.println("Received ack for message = " +  messageId);
      }).publishReceivedHandler(messageId -> {
        endpoint.publishRelease(messageId);
      }).publishCompletionHandler(messageId -> {
        System.out.println("Received ack for message = " +  messageId);
      });

      // Be notified by client keep alive
      endpoint.pingHandler(v -> {
        System.out.println("Ping received from client");
      });

//      // Closing the server
//      mqttServer.close(v -> {
//        System.out.println("MQTT server closed");
//      });

    })
      .listen(ar -> {

        if (ar.succeeded()) {

          System.out.println("MQTT server is listening on port " + ar.result().actualPort());
        } else {

          System.out.println("Error on starting the server");
          ar.cause().printStackTrace();
        }
      });

  }

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    System.out.println("Stop server,All done.");
  }
}
