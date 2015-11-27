package de.fhwedel.coinflip.protocol.model.status;

import java.lang.reflect.Type;
import java.util.Optional;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class ProtocolStatusDeserializer implements JsonDeserializer<ProtocolStatus> {
  @Override
  public ProtocolStatus deserialize(JsonElement jsonElement, Type type,
      JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    int id = jsonElement.getAsInt();
    Optional<ProtocolStatus> protocolStatus = ProtocolStatus.fromId(id);
    return protocolStatus
        .orElseThrow(() -> new JsonParseException(String.valueOf(id) + " is an unknown statusId"));
  }
}
