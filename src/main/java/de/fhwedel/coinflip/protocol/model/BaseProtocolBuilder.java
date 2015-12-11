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
  private PayloadBuilder payloadBuilder = new PayloadBuilder();

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

  public BaseProtocolBuilder setInitialCoin(List<String> coin) {
    payloadBuilder.setInitialCoin(coin);
    return this;
  }

  public BaseProtocolBuilder setEncryptedCoin(List<String> coin) {
    payloadBuilder.setEncryptedCoin(coin);
    return this;
  }

  public BaseProtocolBuilder setDesiredCoin(String coin) {
    payloadBuilder.setDesiredCoin(coin);
    return this;
  }

  public BaseProtocolBuilder setEnChosenCoin(String coin) {
    payloadBuilder.setEnChosenCoin(coin);
    return this;
  }

  public BaseProtocolBuilder setDeChosenCoin(String coin) {
    payloadBuilder.setDeChosenCoin(coin);
    return this;
  }

  public BaseProtocolBuilder setKeyA(List<BigInteger> key) {
    payloadBuilder.setKeyA(key);
    return this;
  }

  public BaseProtocolBuilder setKeyB(List<BigInteger> key) {
    payloadBuilder.setKeyB(key);
    return this;
  }

  public BaseProtocol createBaseProtocol() {
    return new BaseProtocol(id, status, statusMessage,
        negotiationBuilder.createProtocolNegotiation(),
        keyNegotiationBuilder.createKeyNegotiation(), payloadBuilder.createPayload());
  }
}
