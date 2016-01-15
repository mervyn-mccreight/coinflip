package de.fhwedel.coinflip.cipher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;


public class Signer {

  public static PrivateKey readPrivateKeyFromFile(String filename, String password)
      throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException,
      UnrecoverableKeyException {
    File keystoreFile = new File(filename);

    KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
    ks.load(new FileInputStream(keystoreFile), password.toCharArray());

    String alias = ks.aliases().nextElement();

    Key key = ks.getKey(alias, password.toCharArray());
    return (PrivateKey) key;
  }

  public static PublicKey readPublicKeyFromFile(String filename, String password)
      throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
    File keystoreFile = new File(filename);

    KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
    ks.load(new FileInputStream(keystoreFile), password.toCharArray());

    String alias = (ks.aliases()).nextElement();

    Certificate cert = ks.getCertificate(alias);
    return cert.getPublicKey();
  }

  public static byte[] sign(String message, PrivateKey privKey) throws NoSuchAlgorithmException,
      NoSuchProviderException, InvalidKeyException, SignatureException {
    Security.addProvider(new BouncyCastleProvider());

    Signature signature = Signature.getInstance("SHA256with" + privKey.getAlgorithm(), "BC");
    signature.initSign(privKey);
    signature.update(message.getBytes());

    return signature.sign();
  }

  public static boolean verify(String message, byte[] sigBytes, PublicKey pubKey)
      throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException,
      InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException,
      SignatureException {
    Security.addProvider(new BouncyCastleProvider());

    Signature signer = Signature.getInstance("SHA256with" + pubKey.getAlgorithm(), "BC");
    signer.initVerify(pubKey);
    signer.update(message.getBytes());

    return signer.verify(sigBytes);
  }

  // for testing reasons
  public static void main(String[] args)
      throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException,
      CertificateException, IOException, InvalidKeyException, NoSuchProviderException,
      SignatureException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
    Security.addProvider(new BouncyCastleProvider());

    String KEY_ANNA = "ssl-data/client";
    String PASSWORD_KEY = "fhwedel";

    PrivateKey privKey = Signer.readPrivateKeyFromFile(KEY_ANNA, PASSWORD_KEY);

    PublicKey pubKey = Signer.readPublicKeyFromFile(KEY_ANNA, PASSWORD_KEY);

    String message = "Tail";
    byte[] signedData = Signer.sign(message, privKey);

    System.out.println(Signer.verify(message, signedData, pubKey));
  }
}
