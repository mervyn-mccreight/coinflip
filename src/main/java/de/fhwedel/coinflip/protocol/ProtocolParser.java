package de.fhwedel.coinflip.protocol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.fhwedel.coinflip.protocol.id.ProtocolId;
import de.fhwedel.coinflip.protocol.id.ProtocolIdDeserializer;
import de.fhwedel.coinflip.protocol.id.ProtocolIdSerializer;

public class ProtocolParser {
  public BaseProtocol parseJson(String jsonString) {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapter(ProtocolId.class, new ProtocolIdDeserializer());
    gsonBuilder.registerTypeAdapter(ProtocolId.class, new ProtocolIdSerializer());
    Gson gson = gsonBuilder.create();
    return gson.fromJson(jsonString, BaseProtocol.class);
  }
}
