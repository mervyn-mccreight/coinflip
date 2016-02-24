package de.fhwedel.coinflip.config;

public class Configuration {
  private String brokerAddress;

  public String brokerJoinUri() {
    return "https://" + brokerAddress + "/broker/1.0/join";
  }

  public String brokerPlayersUri() {
    return "https://" + brokerAddress + "/broker/1.0/players";
  }
}
