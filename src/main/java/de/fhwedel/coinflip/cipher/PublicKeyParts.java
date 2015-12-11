package de.fhwedel.coinflip.cipher;

import java.math.BigInteger;

public class PublicKeyParts {
  private BigInteger p;
  private BigInteger q;

  public PublicKeyParts(BigInteger p, BigInteger q) {
    this.p = p;
    this.q = q;
  }

  public BigInteger getP() {
    return p;
  }

  public BigInteger getQ() {
    return q;
  }
}
