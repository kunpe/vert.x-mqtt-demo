package dev.imkun.cluster_demo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;

import java.util.Random;

public class DemoWorker extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    EventBus eb = vertx.eventBus();
    eb.consumer("worker.count", mes -> {
      System.out.println("[Worker|" + Thread.currentThread() + "]: Received new request" );
      int count = (int) mes.body();
      count++;
      int random = 0;
      while (random % 99999999 != 78) {
        Random r = new Random();
        random = r.nextInt(99999998) + 1;
      }
      mes.reply(count);
    });
  }

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    System.out.println("stopped");
  }
}
