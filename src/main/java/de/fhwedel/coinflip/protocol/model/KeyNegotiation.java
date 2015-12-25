package de.fhwedel.coinflip.protocol.model;

import java.math.BigInteger;
import java.util.List;

public class KeyNegotiation {
  private BigInteger p;
  private BigInteger q;
  private Integer sid;
  private List<Sids> availableSids;

  public KeyNegotiation(BigInteger p, BigInteger q, Integer sidId, List<Sids> availableSids) {
    this.p = p;
    this.q = q;
    this.sid = sidId;
    this.availableSids = availableSids;
  }

  public BigInteger getP() {
    return this.p;
  }

  public BigInteger getQ() {
    return this.q;
  }

  public Integer getSidId() {
    return this.sid;
  }

  public List<Sids> getAvailableSidsIds() {
    return this.availableSids;
  }
}
