package dev.imkun.cluster_demo;

import io.vertx.core.Launcher;

public class MainLauncher extends Launcher {
  public static void main(String[] args) {
    new MainLauncher().dispatch(new String[] { "run", MainVerticle.class.getName() });
  }
}
