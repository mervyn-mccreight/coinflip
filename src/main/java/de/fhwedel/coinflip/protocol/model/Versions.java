package de.fhwedel.coinflip.protocol.model;

import com.google.common.collect.Sets;

import java.util.Set;

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
}
