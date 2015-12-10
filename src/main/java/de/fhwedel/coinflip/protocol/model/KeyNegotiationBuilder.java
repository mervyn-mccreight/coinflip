package de.fhwedel.coinflip.protocol.model;

import de.fhwedel.coinflip.protocol.model.sid.Sid;

import java.math.BigInteger;
import java.util.List;

public class KeyNegotiationBuilder {
  private BigInteger p;
  private BigInteger q;
  private Sid sid;
  private List<Sids> availableSids;

  public KeyNegotiationBuilder setP(BigInteger p) {
    this.p = p;
    return this;
  }

  public KeyNegotiationBuilder setQ(BigInteger q) {
    this.q = q;
    return this;
  }

  public KeyNegotiationBuilder setSid(Sid sid) {
    this.sid = sid;
    return this;
  }

  public KeyNegotiationBuilder setAvailableSids(List<Sids> availableSids) {
    this.availableSids = availableSids;
    return this;
  }

  public KeyNegotiation createKeyNegotiation() {
    return new KeyNegotiation(p, q, sid, availableSids);
  }
}
