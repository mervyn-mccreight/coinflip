package de.fhwedel.coinflip.protocol.model.sid;

import java.lang.reflect.Type;
import java.util.Optional;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import de.fhwedel.coinflip.protocol.model.id.ProtocolId;

public class SidDeserializer implements JsonDeserializer<Sid> {
  public Sid deserialize(JsonElement jsonElement, Type type,
      JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    Optional<Sid> sid = Sid.fromId(jsonElement.getAsInt());

    if (sid.isPresent()) {
      return sid.get();
    }

    return sid.orElseThrow(() -> new JsonParseException(
        String.valueOf(jsonElement.getAsInt()) + " is an unknown sid"));
  }
}
