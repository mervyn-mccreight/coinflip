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

  private class ProtocolNegotiation {
    private String version;
    private List<Versions> availableVersions;

    private ProtocolNegotiation(String version, List<Versions> availableVersions) {
      this.version = version;
      this.availableVersions = availableVersions;
    }

    public String getNegotiatedVersion() {
      return this.version;
    }

    private class Versions {
      private List<String> versions;

      private Versions(List<String> versions) {
        this.versions = versions;
      }
    }
  }
}
