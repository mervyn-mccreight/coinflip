package de.fhwedel.coinflip.protocol.model.sid;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.fhwedel.coinflip.protocol.model.id.ProtocolId;

// todo (27.11.2015): test serializing of sids
public class SidSerializer implements JsonSerializer<Sid> {
  public JsonElement serialize(Sid sid, Type type,
      JsonSerializationContext jsonSerializationContext) {
    return new JsonPrimitive(sid.getId());
  }
}
