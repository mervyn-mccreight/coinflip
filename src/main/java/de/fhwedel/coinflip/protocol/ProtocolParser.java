package de.fhwedel.coinflip.protocol;

import com.google.gson.Gson;

public class ProtocolParser {
  public Protocol parseJson(String jsonString) {
    Gson gson = new Gson();
    return gson.fromJson(jsonString, Protocol.class);
  }
}
