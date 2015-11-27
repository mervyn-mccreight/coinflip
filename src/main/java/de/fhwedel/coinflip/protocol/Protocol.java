package de.fhwedel.coinflip.protocol;

import java.util.List;

public class Protocol {
  private int protocolId;
  private int statusId;
  private String statusMessage;
  private ProtocolNegotiation protocolNegotiation;

  public Protocol(int protocolId, int statusId, String statusMessage,
      ProtocolNegotiation protocolNegotiation) {
    this.protocolId = protocolId;
    this.statusId = statusId;
    this.statusMessage = statusMessage;
    this.protocolNegotiation = protocolNegotiation;
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

  public String getNegotiatedVersion() {
    return protocolNegotiation.getNegotiatedVersion();
  }

  public List<Versions> getProposedVersions() {
    return protocolNegotiation.getAvailableVersions();
  }
}
