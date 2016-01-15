package de.fhwedel.coinflip.cipher;

import java.math.BigInteger;
import java.security.*;
import java.util.List;

import org.bouncycastle.jcajce.provider.asymmetric.sra.SRADecryptionKeySpec;
import org.bouncycastle.jcajce.provider.asymmetric.sra.SRAKeyGenParameterSpec;
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
    } catch (GeneralSecurityException e) {
      throw new CipherException(e);
    }
  }

  public static KeyPair generateKeyPair(int modulus, BigInteger p, BigInteger q)
      throws CipherException {
    try {
      // create specifications for the key generation.
      SRAKeyGenParameterSpec specs = new SRAKeyGenParameterSpec(modulus, p, q);

      KeyPairGenerator generator =
          KeyPairGenerator.getInstance("SRA", BouncyCastleProvider.PROVIDER_NAME);

      generator.initialize(specs);

      // generate a valid SRA key pair for the given p and q.
      return generator.generateKeyPair();
    } catch (GeneralSecurityException e) {
      throw new CipherException(e);
    }
  }

  public static PrivateKey generatePrivateKey(BigInteger p, BigInteger q, List<BigInteger> parts)
      throws CipherException {
    try {
      SRADecryptionKeySpec spec = new SRADecryptionKeySpec(p, q, parts.get(1), parts.get(0));
      KeyFactory sra = KeyFactory.getInstance("SRA", BouncyCastleProvider.PROVIDER_NAME);
      return sra.generatePrivate(spec);
    } catch (Exception e) {
      throw new CipherException(e);
    }
  }
}
