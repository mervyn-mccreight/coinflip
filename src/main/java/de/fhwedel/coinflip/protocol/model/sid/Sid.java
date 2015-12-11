package de.fhwedel.coinflip.protocol.model.sid;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Optional;

public enum Sid {
  SRA1024SHA1(0, 1024, "SRA/NONE/OAEPPADDING"), SRA2048SHA1(1, 2048,
      "SRA/NONE/OAEPPADDING"), SRA3072SHA1(2, 3072, "SRA/NONE/OAEPPADDING"), SRA1024SHA256(10, 1024,
          "SRA/NONE/OAEPWITHSHA256ANDMGF1PADDING"), SRA2048SHA512(20, 2048,
              "SRA/NONE/OAEPWITHSHA512ANDMGF1PADDING");

  private final int id;
  private final int modulus;
  private final String algorithm;

  Sid(int id, int modulus, String algorithm) {
    this.id = id;
    this.modulus = modulus;
    this.algorithm = algorithm;
  }

  public int getId() {
    return id;
  }

  public int getModulus() {
    return modulus;
  }

  public String getAlgorithm() {
    return algorithm;
  }

  public static Optional<Sid> fromId(int id) {
    return Optional.ofNullable(mapping.get(id));
  }

  private static final Map<Integer, Sid> mapping;

  static {
    mapping = Maps.newHashMap();
    for (Sid sid : Sid.values()) {
      mapping.put(sid.getId(), sid);
    }
  }
}
