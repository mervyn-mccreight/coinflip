package de.fhwedel.coinflip.protocol.status;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Optional;

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
