package de.fhwedel.coinflip.cipher;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import de.fhwedel.coinflip.cipher.exception.CipherException;

public class KeyPairFactory {
  public static KeyPair generateKeyPair(int modulus) throws CipherException {
    try {
      // first get the sra key pair generator instance.
      KeyPairGenerator generator;
      generator = KeyPairGenerator.getInstance("SRA", BouncyCastleProvider.PROVIDER_NAME);

      // provide a bit-size for the key
      generator.initialize(modulus);

      // generate the key pair.
      return generator.generateKeyPair();
    } catch (Exception e) {
      throw new CipherException(e);
    }
  }
}
