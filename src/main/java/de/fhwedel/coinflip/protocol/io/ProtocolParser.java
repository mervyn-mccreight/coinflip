package de.fhwedel.coinflip.protocol.io;

import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import de.fhwedel.coinflip.protocol.model.BaseProtocol;
import de.fhwedel.coinflip.protocol.model.id.ProtocolId;
import de.fhwedel.coinflip.protocol.model.id.ProtocolIdDeserializer;
import de.fhwedel.coinflip.protocol.model.id.ProtocolIdSerializer;
import de.fhwedel.coinflip.protocol.model.status.ProtocolStatus;
import de.fhwedel.coinflip.protocol.model.status.ProtocolStatusDeserializer;
import de.fhwedel.coinflip.protocol.model.status.ProtocolStatusSerializer;

public class ProtocolParser {

  private Gson gson;

  public ProtocolParser() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    // todo (27.11.2015): move serializer to a protocol writer class.
    gsonBuilder.registerTypeAdapter(ProtocolId.class, new ProtocolIdDeserializer());
    gsonBuilder.registerTypeAdapter(ProtocolId.class, new ProtocolIdSerializer());
    gsonBuilder.registerTypeAdapter(ProtocolStatus.class, new ProtocolStatusDeserializer());
    gsonBuilder.registerTypeAdapter(ProtocolStatus.class, new ProtocolStatusSerializer());
    gson = gsonBuilder.create();
  }

  public Optional<BaseProtocol> parseJson(String jsonString) {
    try {
      return Optional.ofNullable(gson.fromJson(jsonString, BaseProtocol.class));
    } catch (JsonSyntaxException e) {
      return Optional.empty();
    }
  }

  public String toJson(BaseProtocol protocol) {
    return gson.toJson(protocol);
  }
}
