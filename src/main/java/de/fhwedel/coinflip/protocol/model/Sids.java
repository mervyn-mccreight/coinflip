package de.fhwedel.coinflip.protocol.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.fhwedel.coinflip.protocol.model.sid.Sid;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Sids {
  private Set<Sid> sids;

  public Sids(Set<Sid> sids) {
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

  public Set<Sid> get() {
    return this.sids;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.sids);
  }

  public static Sids containing(Sid... sids) {
    return new Sids(Sets.newHashSet(sids));
  }
}
