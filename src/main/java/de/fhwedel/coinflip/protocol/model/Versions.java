package de.fhwedel.coinflip.protocol.model;

import java.util.List;

public class Versions {
  private List<String> versions;

  private Versions(List<String> versions) {
    this.versions = versions;
  }

  public List<String> get() {
    return this.versions;
  }

  public int count() {
    return this.versions.size();
  }
}
