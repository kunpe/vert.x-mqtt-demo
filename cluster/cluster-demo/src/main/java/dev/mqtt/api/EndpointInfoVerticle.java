package dev.mqtt.api;

import dev.mqtt.container.MqttSessionContainer;
import dev.mqtt.handler.EndPointMessageHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
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
      if (MqttSessionContainer.mqttSessionContainer.size() > 0) {
        if (Objects.isNull(bodyAsString)) {
          bodyAsString = "Hello";
        }
        JsonObject res = new JsonObject();
        MqttSessionContainer.mqttSessionContainer.forEach((key,value) -> {
          res.put(key,value.mqttTopicSubscriptions().stream().map(Objects::toString).collect(Collectors.joining("")));
        });

        EndPointMessageHandler.publish(topic,bodyAsString,MqttSessionContainer.getByClientId(clientId));
        routingContext.response()
          .putHeader("content-type", "text/plain")
          .end(res.toString());
      } else {
        routingContext.response()
          .putHeader("content-type", "text/plain")
          .end("No mqtt connection");
      }
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
