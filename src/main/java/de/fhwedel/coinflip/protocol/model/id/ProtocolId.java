package de.fhwedel.coinflip.protocol.model.id;

import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Maps;

public enum ProtocolId {
  ZERO(0, "Protocol version negotiation step one"), ONE(1,
      "Protocol version negotiation step two"), TWO(2, "Initialize SID negotiation"), THREE(3,
          "p and q generation"), FOUR(4, "Coin Encryption"), FIVE(5,
              "Coin flip - choose encrypted coin side"), SIX(6,
                  "Coin flip - decrypt chosen coin side and initialize key exchange handshake"), SEVEN(
                      7, "Coin flip - answer key exchange handshake"), ERROR(-1, "Error Status");

  private final int id;
  private final String message;

  ProtocolId(int id, String message) {
    this.id = id;
    this.message = message;
  }

  public int getId() {
    return id;
  }

  public String getMessage() {
    return message;
  }

  public static Optional<ProtocolId> fromId(int id) {
    return Optional.ofNullable(mapping.get(id));
  }

  private static final Map<Integer, ProtocolId> mapping;

  static {
    mapping = Maps.newHashMap();
    for (ProtocolId id : ProtocolId.values()) {
      mapping.put(id.getId(), id);
    }
  }
}
