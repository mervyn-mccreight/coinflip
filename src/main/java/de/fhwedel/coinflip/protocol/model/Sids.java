package de.fhwedel.coinflip.protocol.model;

import de.fhwedel.coinflip.protocol.model.sid.Sid;

import java.util.List;
import java.util.Objects;

public class Sids {
  private List<Sid> sids;

  public Sids(List<Sid> sids) {
    this.sids = sids;
  }

  public int count() {
    return this.sids.size();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (this.getClass() != obj.getClass()) {
      return false;
    }

    Sids that = (Sids) obj;

    return Objects.equals(this.sids, that.sids);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.sids);
  }
}
