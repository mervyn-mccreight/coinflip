package de.fhwedel.coinflip.protocol.model.id;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Optional;

public enum ProtocolId {
  ZERO(0, "Protocol version negotiation step one"), ONE(1, "Protocol version negotiation step two");

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