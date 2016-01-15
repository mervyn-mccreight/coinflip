package de.fhwedel.coinflip.protocol;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.bouncycastle.util.encoders.Hex;

import com.google.common.collect.Lists;

import de.fhwedel.coinflip.CoinFlip;
import de.fhwedel.coinflip.CoinFlipClient;
import de.fhwedel.coinflip.cipher.*;
import de.fhwedel.coinflip.cipher.Signer;
import de.fhwedel.coinflip.cipher.exception.CipherException;
import de.fhwedel.coinflip.protocol.model.BaseProtocol;
import de.fhwedel.coinflip.protocol.model.BaseProtocolBuilder;
import de.fhwedel.coinflip.protocol.model.id.ProtocolId;
import de.fhwedel.coinflip.protocol.model.sid.Sid;
import de.fhwedel.coinflip.protocol.model.status.ProtocolStatus;

public class ClientProtocolHandler implements ProtocolHandler {

  private KeyPair keyPair;

  @Override
  public Optional<BaseProtocol> work(Optional<BaseProtocol> maybeGiven) {

    if (maybeGiven.isPresent()) {
      BaseProtocol given = maybeGiven.get();

      if (given.getStatus() != ProtocolStatus.OK.getId()) {
        return Optional.empty();
      }

      switch (given.getId()) {
        case ONE:
          return Optional.of(handleProtocolStepOne(given));
        case THREE:
          return Optional.of(handleProtocolStepThree(given));

        case FIVE:
          return Optional.of(handleProtocolStepFive(given));
        case SEVEN:
          // todo (18.12.2015): implement last step: validation and check who won :-)
          break;
        case ZERO:
        case TWO:
        case FOUR:
        case SIX:
          return Optional.empty();
      }
    }

    return Optional.empty();
  }

  private BaseProtocol handleProtocolStepFive(BaseProtocol given) {
    // todo (18.12.2015): implement general error checking. this is just the basic for now.

    if (given.getEncryptedChosenCoin() == null || given.getEncryptedChosenCoin().isEmpty()) {
      return new BaseProtocolBuilder().setId(ProtocolId.FIVE).setStatus(ProtocolStatus.ERROR)
          .createBaseProtocol();
    }

    String deChosenCoin;
    PrivateKeyParts parts;
    try {
      deChosenCoin = CryptoEngine.decrypt(Hex.decode(given.getEncryptedChosenCoin()), "SRA",
          keyPair.getPrivate(), Hex::toHexString);
      parts = KeyDataExtractor.getPrivateParts(keyPair);
    } catch (CipherException e) {
      return new BaseProtocolBuilder().setId(ProtocolId.FIVE).setStatus(ProtocolStatus.EXCEPTION)
          .createBaseProtocol();
    }

    String signature;
    try {
      String signatureString = given.getDesiredCoinSide() + given.getEncryptedChosenCoin()
          + Hex.toHexString(parts.getE().toByteArray())
          + Hex.toHexString(parts.getD().toByteArray());
      PrivateKey certificatePrivateKey =
          Signer.readPrivateKeyFromFile("ssl-data/client", "fhwedel");
      signature = Hex.toHexString(Signer.sign(signatureString, certificatePrivateKey));
    } catch (KeyStoreException | UnrecoverableKeyException | IOException | CertificateException
        | NoSuchAlgorithmException | SignatureException | NoSuchProviderException
        | InvalidKeyException e) {
      return new BaseProtocolBuilder().setId(ProtocolId.FIVE).setStatus(ProtocolStatus.EXCEPTION)
          .createBaseProtocol();
    }

    return new BaseProtocolBuilder().setId(ProtocolId.SIX).setStatus(ProtocolStatus.OK)
        .setChosenVersion(given.getNegotiatedVersion())
        .setProposedVersions(given.getProposedVersions())
        .setPublicKeyParts(given.getP(), given.getQ()).setChosenSid(given.getSidId())
        .setAvailableSids(given.getAvailableSidsIds()).setInitialCoin(given.getPlainCoin())
        .setEnChosenCoin(given.getEncryptedChosenCoin()).setEncryptedCoin(given.getEncryptedCoin())
        .setDeChosenCoin(deChosenCoin).setKeyA(Lists.newArrayList(parts.getE(), parts.getD()))
        .setSignature(signature)
        .createBaseProtocol();
  }

  private BaseProtocol handleProtocolStepThree(BaseProtocol given) {
    if (!CoinFlip.supportedSids.get().contains(given.getSidId())) {
      return new BaseProtocolBuilder().setId(ProtocolId.THREE)
          .setStatus(ProtocolStatus.CHOSEN_SID_UNKNOWN).createBaseProtocol();
    }

    // todo (18.12.2015): check for p and q quality.
    if (given.getP() == null || given.getQ() == null) {
      return new BaseProtocolBuilder().setId(ProtocolId.THREE)
          .setStatus(ProtocolStatus.P_OR_Q_MISSING).createBaseProtocol();
    }

    List<String> encryptedCoin;
    try {
      keyPair =
 KeyPairFactory.generateKeyPair(Sid.fromId(given.getSidId()).get().getModulus(),
          given.getP(), given.getQ());
      encryptedCoin = Lists.newArrayList(CoinFlipClient.coin);
      Collections.shuffle(encryptedCoin);

      encryptedCoin.replaceAll(x -> {
        try {
          return CryptoEngine.encrypt(x.getBytes(),
              Sid.fromId(given.getSidId()).get().getAlgorithm(), keyPair.getPublic());
        } catch (CipherException e) {
          throw new RuntimeException(e);
        }
      });

    } catch (CipherException e) {
      return new BaseProtocolBuilder().setId(ProtocolId.THREE).setStatus(ProtocolStatus.EXCEPTION)
          .createBaseProtocol();
    }

    return new BaseProtocolBuilder().setId(ProtocolId.FOUR).setStatus(ProtocolStatus.OK)
        .setChosenVersion(given.getNegotiatedVersion())
        .setProposedVersions(given.getProposedVersions())
        .setPublicKeyParts(given.getP(), given.getQ()).setChosenSid(given.getSidId())
        .setAvailableSids(given.getAvailableSidsIds())
        .setInitialCoin(Lists.newArrayList(CoinFlipClient.coin)).setEncryptedCoin(encryptedCoin)
        .createBaseProtocol();
  }

  private BaseProtocol handleProtocolStepOne(BaseProtocol given) {

    if (!CoinFlip.supportedVersions.get().contains(given.getNegotiatedVersion())) {
      return new BaseProtocolBuilder().setId(ProtocolId.ONE)
          .setStatus(ProtocolStatus.CHOSEN_VERSION_UNKNOWN).createBaseProtocol();
    }

    return new BaseProtocolBuilder().setId(ProtocolId.TWO).setStatus(ProtocolStatus.OK)
        .setProposedVersions(given.getProposedVersions())
        .setChosenVersion(given.getNegotiatedVersion())
        .setAvailableSids(Lists.newArrayList(CoinFlip.supportedSids)).createBaseProtocol();
  }
}
