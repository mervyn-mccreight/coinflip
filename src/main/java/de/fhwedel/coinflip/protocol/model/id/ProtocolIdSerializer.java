package de.fhwedel.coinflip.protocol.model.id;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

// todo (27.11.2015): test serializing of protocol ids.
public class ProtocolIdSerializer implements JsonSerializer<ProtocolId> {
  public JsonElement serialize(ProtocolId protocolId, Type type,
      JsonSerializationContext jsonSerializationContext) {
    return new JsonPrimitive(protocolId.getId());
  }
}
