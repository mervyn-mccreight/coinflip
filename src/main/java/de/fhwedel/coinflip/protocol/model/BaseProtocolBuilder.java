package de.fhwedel.coinflip.protocol.model;

import java.util.List;

import de.fhwedel.coinflip.protocol.model.id.ProtocolId;
import de.fhwedel.coinflip.protocol.model.status.ProtocolStatus;

public class BaseProtocolBuilder {
  private ProtocolId id;
  private ProtocolStatus status;
  private String statusMessage;
  private ProtocolNegotiationBuilder negotiationBuilder = new ProtocolNegotiationBuilder();
  private KeyNegotiation keyNegotiation;

  public BaseProtocolBuilder setId(ProtocolId id) {
    this.id = id;
    return this;
  }

  public BaseProtocolBuilder setStatus(ProtocolStatus status) {
    this.status = status;
    return this;
  }

  public BaseProtocolBuilder setStatusMessage(String statusMessage) {
    this.statusMessage = statusMessage;
    return this;
  }

  public BaseProtocolBuilder setProposedVersions(List<Versions> proposedVersions) {
    negotiationBuilder.setAvailableVersions(proposedVersions);
    return this;
  }

  public BaseProtocolBuilder setChosenVersion(String version) {
    negotiationBuilder.setVersion(version);
    return this;
  }

  public BaseProtocolBuilder setKeyNegotiation(KeyNegotiation keyNegotiation) {
    this.keyNegotiation = keyNegotiation;
    return this;
  }

  public BaseProtocol createBaseProtocol() {
    return new BaseProtocol(id, status, statusMessage,
        negotiationBuilder.createProtocolNegotiation(), keyNegotiation);
  }
}
