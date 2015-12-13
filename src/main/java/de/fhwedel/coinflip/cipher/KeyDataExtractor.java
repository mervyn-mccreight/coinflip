package de.fhwedel.coinflip.cipher;

import java.security.KeyFactory;
import java.security.KeyPair;

import org.bouncycastle.jcajce.provider.asymmetric.sra.SRADecryptionKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import de.fhwedel.coinflip.cipher.exception.CipherException;

public class KeyDataExtractor {
  public static PublicKeyParts getPublicKeyParts(KeyPair keyPair) throws CipherException {
    try {
      // get a key factory instance for SRA
      KeyFactory factory = KeyFactory.getInstance("SRA", BouncyCastleProvider.PROVIDER_NAME);
      // extract p and q. you have to use the private key for this, since only the private key
      // contains the information.
      // the key factory fetches the hidden information out of the private key and fills a
      // SRADecryptionKeySpec to provide the information.
      SRADecryptionKeySpec spec =
          factory.getKeySpec(keyPair.getPrivate(), SRADecryptionKeySpec.class);

      return new PublicKeyParts(spec.getP(), spec.getQ());
    } catch (Exception e) {
      throw new CipherException(e);
    }
  }

  public static PrivateKeyParts getPrivateParts(KeyPair keyPair) throws CipherException {
    try {
      // get a key factory instance for SRA
      KeyFactory factory = KeyFactory.getInstance("SRA", BouncyCastleProvider.PROVIDER_NAME);
      // extract p and q. you have to use the private key for this, since only the private key
      // contains the information.
      // the key factory fetches the hidden information out of the private key and fills a
      // SRADecryptionKeySpec to provide the information.
      SRADecryptionKeySpec spec =
          factory.getKeySpec(keyPair.getPrivate(), SRADecryptionKeySpec.class);

      return new PrivateKeyParts(spec.getD(), spec.getE());

    } catch (Exception e) {
      throw new CipherException(e);
    }
  }
}
