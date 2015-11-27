package de.fhwedel.coinflip.protocol.id;

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
}
