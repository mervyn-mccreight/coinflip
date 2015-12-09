package de.fhwedel.coinflip.protocol.model;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.SerializedName;

import de.fhwedel.coinflip.protocol.model.id.ProtocolId;
import de.fhwedel.coinflip.protocol.model.status.ProtocolStatus;

public class BaseProtocol {
  @SerializedName("protocolId")
  private ProtocolId id;

  @SerializedName("statusId")
  private ProtocolStatus status;

  private String statusMessage;

  private ProtocolNegotiation protocolNegotiation;

  private KeyNegotiation keyNegotiation;

  private Payload payload;

  public BaseProtocol(ProtocolId id, ProtocolStatus status, String statusMessage,
      ProtocolNegotiation protocolNegotiation, KeyNegotiation keyNegotiation) {
    this.id = id;
    this.status = status;
    this.statusMessage = statusMessage;
    this.protocolNegotiation = protocolNegotiation;
    this.keyNegotiation = keyNegotiation;
  }

  public int getStep() {
    return this.id.getId();
  }

  public int getStatus() {
    return this.status.getId();
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

  public BigInteger getP() {
    return this.keyNegotiation.getP();
  }

  public BigInteger getQ() {
    return this.keyNegotiation.getQ();
  }

  public int getSid() {
    return this.keyNegotiation.getSid();
  }

  public List<Sids> getAvailableSids() {
    return this.keyNegotiation.getAvailableSids();
  }

  public List<String> getPlainCoin() {
    return this.payload.getInitialCoin();
  }

  public String getDesiredCoinSide() {
    return this.payload.getDesiredCoin();
  }

  public List<String> getEncryptedCoin() {
    return this.payload.getEncryptedCoin();
  }

  public String getEncryptedChosenCoin() {
    return this.payload.getEnChosenCoin();
  }

  public String getDecryptedChosenCoin() {
    return this.payload.getDeChosenCoin();
  }

  public List<BigInteger> getPrivateParametersForKeyA() {
    return this.payload.getKeyA();
  }

  public List<BigInteger> getPrivateParametersForKeyB() {
    return this.payload.getKeyB();
  }

  public ProtocolId getId() {
    return this.id;
  }

  public void addProposedVersions(Versions versions) {
    this.protocolNegotiation.addAvailableVersion(versions);
  }

  public ProtocolNegotiation getProtocolNegotiation() {
    return protocolNegotiation;
  }

  public void setChosenVersion(String chosenVersion) {
    this.protocolNegotiation.setVersion(chosenVersion);
  }

  public void setProtocolId(ProtocolId protocolId) {
    this.id = protocolId;
  }
}
