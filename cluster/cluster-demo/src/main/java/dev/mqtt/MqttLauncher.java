package dev.mqtt;

import dev.imkun.cluster_demo.DemoVerticle;
import dev.imkun.cluster_demo.MainVerticle;
import dev.mqtt.api.EndpointInfoVerticle;
import io.vertx.core.Launcher;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class MqttLauncher extends Launcher {
  public static Vertx vertx = null;
  public static void main(String[] args) {
//    new MqttLauncher().dispatch(new String[] { "run", MqttServer.class.getName()});
    ClusterManager mgr = new HazelcastClusterManager();
    VertxOptions options = new VertxOptions().setClusterManager(mgr).setClusterHost("192.168.3.23");
    Vertx.clusteredVertx(options, res -> {
      if (res.succeeded()) {
        vertx = res.result();
        vertx.deployVerticle(new EndpointInfoVerticle());
        vertx.deployVerticle(new MqttServer());
      }
    });
  }

}
