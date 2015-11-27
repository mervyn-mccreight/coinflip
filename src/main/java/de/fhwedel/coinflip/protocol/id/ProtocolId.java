package de.fhwedel.coinflip.protocol.id;

public enum ProtocolId {
  ZERO(0, "Protocolnegotiation 1");

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
