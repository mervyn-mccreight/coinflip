package de.fhwedel.coinflip.protocol;

public class Protocol {
  private int protocolId;
  private int statusId;
  private String statusMessage;

  public Protocol(int protocolId, int statusId, String statusMessage) {
    this.protocolId = protocolId;
    this.statusId = statusId;
    this.statusMessage = statusMessage;
  }

  public int getVersion() {
    return this.protocolId;
  }

  public int getStatus() {
    return this.statusId;
  }

  public String getStatusMessage() {
    return this.statusMessage;
  }
}
