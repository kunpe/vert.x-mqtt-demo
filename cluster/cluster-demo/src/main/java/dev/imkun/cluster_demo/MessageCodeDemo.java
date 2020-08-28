package dev.imkun.cluster_demo;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class MessageCodeDemo implements MessageCodec {
  @Override
  public void encodeToWire(Buffer buffer, Object o) {

  }

  @Override
  public Object decodeFromWire(int pos, Buffer buffer) {
    return null;
  }

  @Override
  public Object transform(Object o) {
    return null;
  }

  @Override
  public String name() {
    return null;
  }

  @Override
  public byte systemCodecID() {
    return 0;
  }
}
