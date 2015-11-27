package de.fhwedel.coinflip.protocol.status;

public enum ProtocolStatus {
  OK(0, "OK");

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
}
