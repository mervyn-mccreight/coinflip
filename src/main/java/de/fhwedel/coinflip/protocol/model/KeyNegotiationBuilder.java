package de.fhwedel.coinflip.protocol.model;

import java.math.BigInteger;
import java.util.List;

public class KeyNegotiationBuilder {
  private BigInteger p;
  private BigInteger q;
  private Integer sidId;
  private List<Sids> availableSids;

  public KeyNegotiationBuilder setP(BigInteger p) {
    this.p = p;
    return this;
  }

  public KeyNegotiationBuilder setQ(BigInteger q) {
    this.q = q;
    return this;
  }

  public KeyNegotiationBuilder setSidId(Integer sidId) {
    this.sidId = sidId;
    return this;
  }

  public KeyNegotiationBuilder setAvailableSids(List<Sids> availableSids) {
    this.availableSids = availableSids;
    return this;
  }

  public KeyNegotiation createKeyNegotiation() {
    return new KeyNegotiation(p, q, sidId, availableSids);
  }
}
