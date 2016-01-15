package de.fhwedel.coinflip.protocol.model;

import java.util.List;

public class ProtocolNegotiationBuilder {
  private String version;
  private List<Versions> availableVersions;

  public ProtocolNegotiationBuilder setVersion(String version) {
    this.version = version;
    return this;
  }

  public ProtocolNegotiationBuilder setAvailableVersions(List<Versions> availableVersions) {
    this.availableVersions = availableVersions;
    return this;
  }

  public ProtocolNegotiation createProtocolNegotiation() {
    if (version == null && availableVersions == null) {
      return null;
    }
    return new ProtocolNegotiation(version, availableVersions);
  }
}
