package de.fhwedel.coinflip.protocol.model;

import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;

public class Versions {
  private Set<String> versions;

  private Versions(Set<String> versions) {
    this.versions = versions;
  }

  public static Versions containing(String... versionStrings) {
    return new Versions(Sets.newHashSet(versionStrings));
  }

  public Set<String> get() {
    return this.versions;
  }

  public int count() {
    return this.versions.size();
  }

  @Override
  public boolean equals(Object obj) {

    if (obj == null) {
      return false;
    }

    if (obj.getClass() != this.getClass()) {
      return false;
    }

    Versions that = (Versions) obj;

    return Objects.equal(this.versions, that.versions);
  }
}
