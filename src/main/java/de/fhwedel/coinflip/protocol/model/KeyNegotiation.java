package de.fhwedel.coinflip.protocol.model;

import de.fhwedel.coinflip.protocol.model.sid.Sid;

import java.math.BigInteger;
import java.util.List;

public class KeyNegotiation {
  private BigInteger p;
  private BigInteger q;
  private Sid sid;
  private List<Sids> availableSids;

  public KeyNegotiation(BigInteger p, BigInteger q, Sid sid, List<Sids> availableSids) {
    this.p = p;
    this.q = q;
    this.sid = sid;
    this.availableSids = availableSids;
  }

  public BigInteger getP() {
    return this.p;
  }

  public BigInteger getQ() {
    return this.q;
  }

  public Sid getSid() {
    return this.sid;
  }

  public List<Sids> getAvailableSids() {
    return this.availableSids;
  }
}
