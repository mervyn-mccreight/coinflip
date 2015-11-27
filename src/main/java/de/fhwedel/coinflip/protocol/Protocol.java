package de.fhwedel.coinflip.protocol;

import java.util.List;

public class Protocol {
  // todo (27.11.2015): custom de-/serializer for ProtocolId-Enum.
  private int protocolId;

  // todo (27.11.2015): custom de-/serializer for ProtocolStatus-Enum.
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

  public int getStep() {
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
