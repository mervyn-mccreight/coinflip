package de.fhwedel.coinflip.protocol.model;

import java.math.BigInteger;
import java.util.List;

public class PayloadBuilder {
  private List<String> initialCoin;
  private String desiredCoin;
  private List<String> encryptedCoin;
  private String enChosenCoin;
  private String deChosenCoin;
  private List<BigInteger> keyA;
  private List<BigInteger> keyB;
  private String signature;

  public PayloadBuilder setInitialCoin(List<String> initialCoin) {
    this.initialCoin = initialCoin;
    return this;
  }

  public PayloadBuilder setDesiredCoin(String desiredCoin) {
    this.desiredCoin = desiredCoin;
    return this;
  }

  public PayloadBuilder setEncryptedCoin(List<String> encryptedCoin) {
    this.encryptedCoin = encryptedCoin;
    return this;
  }

  public PayloadBuilder setEnChosenCoin(String enChosenCoin) {
    this.enChosenCoin = enChosenCoin;
    return this;
  }

  public PayloadBuilder setDeChosenCoin(String deChosenCoin) {
    this.deChosenCoin = deChosenCoin;
    return this;
  }

  public PayloadBuilder setKeyA(List<BigInteger> keyA) {
    this.keyA = keyA;
    return this;
  }

  public PayloadBuilder setSignature(String sig) {
    this.signature = sig;
    return this;
  }

  public PayloadBuilder setKeyB(List<BigInteger> keyB) {
    this.keyB = keyB;
    return this;
  }

  public Payload createPayload() {
    if (initialCoin == null && desiredCoin == null && encryptedCoin == null && enChosenCoin == null
        && deChosenCoin == null && keyA == null && keyB == null && signature == null) {
      return null;
    }

    return new Payload(initialCoin, desiredCoin, encryptedCoin, enChosenCoin, deChosenCoin, keyA,
        keyB, signature);
  }
}
