package dev.mqtt.utils;

import dev.mqtt.entity.MqttSession;
import dev.mqtt.exception.MqttSessionNotExistException;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.mqtt.MqttTopicSubscription;

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class MqttSessionUtils {
  private static final Logger log = LoggerFactory.getLogger(MqttSessionUtils.class);
  private static final String MQTT_SESSION_KEY = "mqtt_session";

  public static void put(String clientId, MqttSession mqttSession, Vertx vertx) {
    // mqtt share data
    SharedData sharedData = vertx.sharedData();
    sharedData.<String, MqttSession>getAsyncMap(MQTT_SESSION_KEY, res -> {
      if (res.succeeded()) {
        AsyncMap<String, MqttSession> map = res.result();
        map.put(clientId,mqttSession,handler -> {
          if (handler.succeeded()) {
            log.info(clientId + " has store.");
          } else {
            log.error("Store the client ["+ clientId +"] error.Cause"+handler.cause());
          }
        });
      } else {
        // Something went wrong!
      }
    });
  }

  public static MqttSession get(String clientId, Vertx vertx) {
    AtomicReference<MqttSession> mqttSession = new AtomicReference<>();
    // mqtt share data
    SharedData sharedData = vertx.sharedData();
    sharedData.<String, MqttSession>getAsyncMap(MQTT_SESSION_KEY, res -> {
      if (res.succeeded()) {
        AsyncMap<String, MqttSession> map = res.result();
        map.get(clientId,handler -> {
          if (handler.succeeded()) {
            if (Objects.isNull(handler.result())) {
              throw new MqttSessionNotExistException("Session not exist");
            }
            mqttSession.set(handler.result());
          } else {
            log.error("Store the client ["+ clientId +"] error.Cause"+handler.cause());
          }
        });
      } else {
        // Something went wrong!
        log.error("Store the client ["+ clientId +"] error.Cause"+res.cause());
      }
    });
    return mqttSession.get();
  }

  public static void remove(String clientId, Vertx vertx) {
    // mqtt share data
    SharedData sharedData = vertx.sharedData();
    sharedData.<String, MqttSession>getAsyncMap(MQTT_SESSION_KEY, res -> {
      if (res.succeeded()) {
        AsyncMap<String, MqttSession> map = res.result();
        map.remove(clientId,handler -> {
          if (handler.succeeded()) {
            log.info("Remove the client ["+ clientId +"] success");
          } else {
            log.error("Store the client ["+ clientId +"] error.Cause"+handler.cause());
          }
        });
      } else {
        // Something went wrong!
        log.error("Store the client ["+ clientId +"] error.Cause"+res.cause());
      }
    });
  }

  public static void updateSubscription(String clientId, MqttTopicSubscription mqttTopicSubscription,Vertx vertx) {
    // mqtt share data
    SharedData sharedData = vertx.sharedData();
    sharedData.<String, MqttSession>getAsyncMap(MQTT_SESSION_KEY, res -> {
      if (res.succeeded()) {
        AsyncMap<String, MqttSession> map = res.result();
        map.get(clientId,handler -> {
          if (handler.succeeded()) {
            if (Objects.nonNull(handler) && Objects.nonNull(handler.result())) {
              handler.result().mqttTopicSubscriptions().add(mqttTopicSubscription);
            }
          } else {
            log.error("Update the client ["+ clientId +"] error.Cause"+handler.cause());
          }
        });
      } else {
        // Something went wrong!
        log.error("Update the client ["+ clientId +"] error.Cause"+res.cause());
      }
    });
  }

  public static void removeSubscription(String clientId, String topic,Vertx vertx) {
    // mqtt share data
    SharedData sharedData = vertx.sharedData();
    sharedData.<String, MqttSession>getAsyncMap(MQTT_SESSION_KEY, res -> {
      if (res.succeeded()) {
        AsyncMap<String, MqttSession> map = res.result();
        map.get(clientId,handler -> {
          if (handler.succeeded()) {
            Iterator<MqttTopicSubscription> iterator = handler.result().mqttTopicSubscriptions().iterator();
            while (iterator.hasNext()) {
              if (Objects.equals(iterator.next().topicName(),topic)) {
                iterator.remove();
                break;
              }
            }
          } else {
            log.error("Remove the client ["+ clientId +"] error.Cause"+handler.cause());
          }
        });
      } else {
        // Something went wrong!
        log.error("Remove the client ["+ clientId +"] error.Cause"+res.cause());
      }
    });
  }

  }
