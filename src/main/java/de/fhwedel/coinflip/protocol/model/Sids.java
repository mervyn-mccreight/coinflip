package de.fhwedel.coinflip.protocol.model;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.annotations.SerializedName;
import de.fhwedel.coinflip.protocol.model.sid.Sid;

public class Sids {
  @SerializedName("sids")
  private Set<Integer> sidIds;

  public Sids(Set<Integer> sids) {
    this.sidIds = sids;
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

    return Objects.equals(this.sidIds, that.sidIds);
  }

  public Set<Integer> get() {
    return this.sidIds;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.sidIds);
  }

  public static Sids containing(Sid... sids) {
    return new Sids(Arrays.stream(sids).mapToInt(Sid::getId).boxed().collect(Collectors.toSet()));
  }

  @Override
  public String toString() {
    return "Sids{" + "sidIds=" + sidIds + '}';
  }
}
