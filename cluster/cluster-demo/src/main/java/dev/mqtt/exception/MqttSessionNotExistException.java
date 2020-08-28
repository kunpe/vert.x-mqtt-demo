package dev.mqtt.exception;

public class MqttSessionNotExistException extends RuntimeException {
  public MqttSessionNotExistException() {
    super();
  }

  public MqttSessionNotExistException(String message) {
    super(message);
  }

  public MqttSessionNotExistException(String message, Throwable cause) {
    super(message, cause);
  }

  public MqttSessionNotExistException(Throwable cause) {
    super(cause);
  }

  protected MqttSessionNotExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
