package de.fhwedel.coinflip.cipher;

import java.math.BigInteger;

public class PrivateKeyParts {
  private BigInteger d;
  private BigInteger e;

  public PrivateKeyParts(BigInteger d, BigInteger e) {
    this.d = d;
    this.e = e;
  }

  public BigInteger getD() {
    return d;
  }

  public BigInteger getE() {
    return e;
  }
}
