package de.fhwedel.coinflip.protocol.model;

import java.math.BigInteger;
import java.util.List;

import de.fhwedel.coinflip.protocol.model.id.ProtocolId;
import de.fhwedel.coinflip.protocol.model.sid.Sid;
import de.fhwedel.coinflip.protocol.model.status.ProtocolStatus;

public class BaseProtocolBuilder {
  private ProtocolId id;
  private ProtocolStatus status;
  private String statusMessage;
  private ProtocolNegotiationBuilder negotiationBuilder = new ProtocolNegotiationBuilder();
  private KeyNegotiationBuilder keyNegotiationBuilder = new KeyNegotiationBuilder();

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

  public BaseProtocolBuilder setChosenSid(Sid sid) {
    keyNegotiationBuilder.setSid(sid);
    return this;
  }

  public BaseProtocolBuilder setAvailableSids(List<Sids> newSids) {
    keyNegotiationBuilder.setAvailableSids(newSids);
    return this;
  }

  public BaseProtocolBuilder setPublicKeyParts(BigInteger p, BigInteger q) {
    keyNegotiationBuilder.setP(p);
    keyNegotiationBuilder.setQ(q);
    return this;
  }

  public BaseProtocol createBaseProtocol() {
    return new BaseProtocol(id, status, statusMessage,
        negotiationBuilder.createProtocolNegotiation(),
        keyNegotiationBuilder.createKeyNegotiation());
  }
}
