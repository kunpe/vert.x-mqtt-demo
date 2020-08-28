package dev.imkun.mqttserver;

import io.vertx.core.Launcher;

public class MqttServerLauncher extends Launcher {
  public static void main(String[] args) {
    new MqttServerLauncher().dispatch(new String[] { "run", MqttServerVerticle.class.getName() });
  }
}
