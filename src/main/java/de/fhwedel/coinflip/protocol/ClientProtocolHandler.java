package de.fhwedel.coinflip.protocol;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.swing.*;

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
  private Optional<JLabel> progressLabel;
  private Optional<JProgressBar> progressBar;

  public ClientProtocolHandler(Optional<JLabel> progressLabel, Optional<JProgressBar> progressBar) {
    this.progressLabel = progressLabel;
    this.progressBar = progressBar;
  }

  @Override
  public Optional<BaseProtocol> work(Optional<BaseProtocol> maybeGiven) {

    if (maybeGiven.isPresent()) {
      BaseProtocol given = maybeGiven.get();

      if (given.getStatus() != ProtocolStatus.OK.getId()) {
        this.progressLabel.ifPresent(label -> label.setText("The server sent an error."));
        return Optional.empty();
      }

      switch (given.getId()) {
        case ONE:
          this.progressLabel.ifPresent(label -> label.setText("Version negotiation finished."));
          this.progressBar.ifPresent(bar -> bar.setValue(3));
          return Optional.of(handleProtocolStepOne(given));
        case THREE:
          this.progressLabel
              .ifPresent(label -> label.setText("Sig negotiation finished. Received modulus."));
          this.progressBar.ifPresent(bar -> bar.setValue(5));
          return Optional.of(handleProtocolStepThree(given));
        case FIVE:
          this.progressLabel.ifPresent(
              label -> label.setText("Decrypting chosen coin side and sending signature."));
          this.progressBar.ifPresent(bar -> bar.setValue(7));
          return Optional.of(handleProtocolStepFive(given));
        case SEVEN:
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

  public String determineCoinResult(BaseProtocol seven) throws CipherException {
    String decryptedChosenCoin = seven.getDecryptedChosenCoin();
    return CryptoEngine.decrypt(Hex.decode(decryptedChosenCoin),
        Sid.fromId(seven.getSidId()).get().getAlgorithm(), KeyPairFactory.generatePrivateKey(
            seven.getP(), seven.getQ(), seven.getPrivateParametersForKeyB()),
        String::new);
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

    this.progressLabel
        .ifPresent(label -> label.setText("Decrypting chosen coin side and sending signature."));
    this.progressBar.ifPresent(bar -> bar.setValue(8));

    return new BaseProtocolBuilder().setId(ProtocolId.SIX).setStatus(ProtocolStatus.OK)
        .setChosenVersion(given.getNegotiatedVersion())
        .setProposedVersions(given.getProposedVersions())
        .setPublicKeyParts(given.getP(), given.getQ()).setChosenSid(given.getSidId())
        .setAvailableSids(given.getAvailableSidsIds()).setInitialCoin(given.getPlainCoin())
        .setEnChosenCoin(given.getEncryptedChosenCoin()).setEncryptedCoin(given.getEncryptedCoin())
        .setDeChosenCoin(deChosenCoin).setKeyA(Lists.newArrayList(parts.getE(), parts.getD()))
        .setSignature(signature)
.setDesiredCoin(given.getDesiredCoinSide())
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

    this.progressLabel.ifPresent(label -> label.setText("Choosing and encrypting coin-side."));
    this.progressBar.ifPresent(bar -> bar.setValue(6));

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

    this.progressLabel.ifPresent(label -> label.setText("Proposing supported sids."));
    this.progressBar.ifPresent(bar -> bar.setValue(4));

    return new BaseProtocolBuilder().setId(ProtocolId.TWO).setStatus(ProtocolStatus.OK)
        .setProposedVersions(given.getProposedVersions())
        .setChosenVersion(given.getNegotiatedVersion())
        .setAvailableSids(Lists.newArrayList(CoinFlip.supportedSids)).createBaseProtocol();
  }
}
