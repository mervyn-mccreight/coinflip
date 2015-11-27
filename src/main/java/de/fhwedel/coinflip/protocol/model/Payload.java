package de.fhwedel.coinflip.protocol.model;

import java.math.BigInteger;
import java.util.List;

import com.google.common.collect.Lists;

public class Payload {
  // todo (27.11.2015): maybe coin class?
  private List<String> initialCoin;
  private String desiredCoin;

  private List<String> encryptedCoin;
  private String enChosenCoin;
  private String deChosenCoin;

  private List<BigInteger> keyA;
  private List<BigInteger> keyB;

  public Payload(List<String> initialCoin, String desiredCoin, List<String> encryptedCoin,
      String enChosenCoin, String deChosenCoin, List<BigInteger> keyA, List<BigInteger> keyB) {
    this.initialCoin = initialCoin;
    this.desiredCoin = desiredCoin;
    this.encryptedCoin = encryptedCoin;
    this.enChosenCoin = enChosenCoin;
    this.deChosenCoin = deChosenCoin;
    this.keyA = keyA;
    this.keyB = keyB;
  }

  public List<String> getInitialCoin() {
    return Lists.newArrayList(this.initialCoin);
  }

  // todo (27.11.2015): signature missing, but i do not know that it is atm.
  // lol @ protocol.
}
