package de.fhwedel.coinflip.protocol.model.status;

import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Maps;

public enum ProtocolStatus {
  OK(0, "OK"), ERROR(10, "ERROR"), NO_VERSION(1, "NO_VERSION"), EXCEPTION(20,
      "SERVER-SIDE ERROR"), UNKNOWN_PROTOCOL(30, "UNKNOWN_PROTOCOL_ID"), NO_KEY(40,
          "NO_KEY_GIVEN"), UNEXPECTED_ID(41, "UNEXPECTED_PROTOCOL_ID GIVEN");

  private final int id;
  private final String message;

  ProtocolStatus(int id, String message) {
    this.id = id;
    this.message = message;
  }

  public int getId() {
    return id;
  }

  public String getMessage() {
    return message;
  }

  public static Optional<ProtocolStatus> fromId(int id) {
    return Optional.ofNullable(mapping.get(id));
  }

  private static final Map<Integer, ProtocolStatus> mapping;

  static {
    mapping = Maps.newHashMap();
    for (ProtocolStatus status : ProtocolStatus.values()) {
      mapping.put(status.getId(), status);
    }
  }
}
