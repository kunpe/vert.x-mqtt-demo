package dev.mqtt.api;

import dev.mqtt.entity.MqttSession;
import dev.mqtt.handler.EndPointMessageHandler;
import dev.mqtt.utils.MqttSessionUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

import java.util.Objects;
import java.util.stream.Collectors;

public class EndpointInfoVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    HttpServer server = vertx.createHttpServer();
    Router router = Router.router(vertx);
    router.route(HttpMethod.GET,"/topic/:clientId/:topic").handler(routingContext -> {
      String topic = routingContext.pathParam("topic");
      String clientId = routingContext.pathParam("clientId");
      String bodyAsString = routingContext.getBodyAsString();
      bodyAsString = "Hello";
      MqttSession mqttSession = MqttSessionUtils.get(clientId, vertx);
      EndPointMessageHandler.publish(topic,bodyAsString,mqttSession.endpoint());
        routingContext.response()
          .putHeader("content-type", "text/plain")
          .end("No mqtt connection");
    });
    server.requestHandler(router).listen(1234,handler -> {
      if (handler.succeeded()) {
        System.out.println("Api 服务启动成功");
      }
    });
  }

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {

  }
}
