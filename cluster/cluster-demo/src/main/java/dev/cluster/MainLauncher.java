package dev.cluster;

import dev.imkun.cluster_demo.MainVerticle;
import io.vertx.core.Launcher;

public class MainLauncher extends Launcher {
  public static void main(String[] args) {
    new MainLauncher().dispatch(new String[] { "run", DataVerticle.class.getName() });
  }
}
