package dev.cluster;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.web.Router;

import java.util.concurrent.atomic.AtomicReference;

public class HttpVerticle extends AbstractVerticle {
  private static final Logger log = LoggerFactory.getLogger(HttpVerticle.class);
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Router router = Router.router(vertx);
    HttpServer server = vertx.createHttpServer();
    router.get("/").handler(routingContext -> {
      AtomicReference<Object> val = null;
      SharedData sharedData = vertx.sharedData();

      sharedData.<String, String>getLocalAsyncMap("mymap", res -> {
        if (res.succeeded()) {
          // Local-only async map
          AsyncMap<String, String> map = res.result();
          map.get("foo", resGet -> {
            if (resGet.succeeded()) {
              // Successfully got the value
              val.getAndSet(resGet.result());
            } else {
              // Something went wrong!
            }
          });
        } else {
          // Something went wrong!
        }
      });

      // This handler will be called for every request
      HttpServerResponse response = routingContext.response();
      response.putHeader("content-type", "text/plain");

      // Write to the response and end it
      response.end("HttpVerticle:"+val.get());
    });

    server.requestHandler(router).listen(1234,res -> {
      if (res.succeeded()) {
        log.info("Http Server startup.");
      } else {
        log.error("Http Server error.");
      }
    });
  }

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    log.info("Http Server closed.");
  }
}
