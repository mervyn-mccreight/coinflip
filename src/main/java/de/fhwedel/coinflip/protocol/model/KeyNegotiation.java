package de.fhwedel.coinflip.protocol.model;

import java.math.BigInteger;
import java.util.List;

public class KeyNegotiation {
  private BigInteger p;
  private BigInteger q;
  private int sid;
  private List<Sids> availableSids;

  public BigInteger getP() {
    return this.p;
  }

  public BigInteger getQ() {
    return this.q;
  }

  public int getSid() {
    return this.sid;
  }

  public List<Sids> getAvailableSids() {
    return this.availableSids;
  }
}
