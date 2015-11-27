package de.fhwedel.coinflip.protocol.model;

import java.util.List;

public class ProtocolNegotiation {
  private String version;
  private List<Versions> availableVersions;

  private ProtocolNegotiation(String version, List<Versions> availableVersions) {
    this.version = version;
    this.availableVersions = availableVersions;
  }

  public String getNegotiatedVersion() {
    return this.version;
  }

  public List<Versions> getAvailableVersions() {
    return this.availableVersions;
  }

}
