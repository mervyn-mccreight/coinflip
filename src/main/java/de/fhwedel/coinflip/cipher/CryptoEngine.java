package de.fhwedel.coinflip.cipher;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.function.Function;

import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import de.fhwedel.coinflip.cipher.exception.CipherException;

public class CryptoEngine {
  public static String encrypt(byte[] data, String algorithm, PublicKey key)
      throws CipherException {
    try {
      Cipher engine = Cipher.getInstance(algorithm, BouncyCastleProvider.PROVIDER_NAME);
      engine.init(Cipher.ENCRYPT_MODE, key);
      byte[] cipher = engine.doFinal(data);
      return Hex.toHexString(cipher);
    } catch (Exception e) {
      throw new CipherException(e);
    }
  }

  public static String decrypt(byte[] data, String algorithm, PrivateKey key,
      Function<byte[], String> resultMapper)
      throws CipherException {
    try {
      Cipher engine = Cipher.getInstance(algorithm, BouncyCastleProvider.PROVIDER_NAME);
      engine.init(Cipher.DECRYPT_MODE, key);
      byte[] plain = engine.doFinal(data);

      return resultMapper.apply(plain);
    } catch (Exception e) {
      throw new CipherException(e);
    }
  }
}
