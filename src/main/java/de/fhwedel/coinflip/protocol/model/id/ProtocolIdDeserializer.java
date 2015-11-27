package de.fhwedel.coinflip.protocol.model.id;

import java.lang.reflect.Type;
import java.util.Optional;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class ProtocolIdDeserializer implements JsonDeserializer<ProtocolId> {
  public ProtocolId deserialize(JsonElement jsonElement, Type type,
      JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    Optional<ProtocolId> protocolId = ProtocolId.fromId(jsonElement.getAsInt());

    if (protocolId.isPresent()) {
      return protocolId.get();
    }

    return protocolId.orElseThrow(() -> new JsonParseException(
        String.valueOf(jsonElement.getAsInt()) + " is an unknown protocolId"));
  }
}
