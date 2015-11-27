package de.fhwedel.coinflip.protocol.status;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class ProtocolStatusSerializer implements JsonSerializer<ProtocolStatus> {
  @Override
  public JsonElement serialize(ProtocolStatus protocolStatus, Type type,
      JsonSerializationContext jsonSerializationContext) {
    return new JsonPrimitive(protocolStatus.getId());
  }
}
