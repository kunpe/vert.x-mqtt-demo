package dev.imkun.cluster_demo;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class Launcher1 {
  public static EventBus clusterEB = null;

  public static void main(String[] args) {
    ClusterManager mgr = new HazelcastClusterManager();

    VertxOptions options = new VertxOptions().setClusterManager(mgr).setClusterHost("192.168.3.23");
    Vertx.clusteredVertx(options, res -> {
      if (res.succeeded()) {
        Vertx vertx = res.result();
        clusterEB = vertx.eventBus();
        vertx.deployVerticle(new DemoVerticle());
      }
    });
  }
}
