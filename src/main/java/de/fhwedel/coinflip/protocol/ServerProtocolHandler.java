package de.fhwedel.coinflip.protocol;

import java.math.BigInteger;
import java.security.KeyPair;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.bouncycastle.util.encoders.Hex;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.fhwedel.coinflip.CoinFlip;
import de.fhwedel.coinflip.CoinFlipServer;
import de.fhwedel.coinflip.cipher.*;
import de.fhwedel.coinflip.cipher.exception.CipherException;
import de.fhwedel.coinflip.protocol.model.BaseProtocol;
import de.fhwedel.coinflip.protocol.model.BaseProtocolBuilder;
import de.fhwedel.coinflip.protocol.model.Sids;
import de.fhwedel.coinflip.protocol.model.Versions;
import de.fhwedel.coinflip.protocol.model.id.ProtocolId;
import de.fhwedel.coinflip.protocol.model.sid.Sid;
import de.fhwedel.coinflip.protocol.model.status.ProtocolStatus;

public class ServerProtocolHandler implements ProtocolHandler {

  private int sessionId;

  public ServerProtocolHandler(int sessionId) {
    this.sessionId = sessionId;
  }

  @Override
  public Optional<BaseProtocol> work(Optional<BaseProtocol> protocol) {
    if (protocol.isPresent()) {
      BaseProtocol baseProtocol = protocol.get();
      switch (baseProtocol.getId()) {
        case ZERO:
          return Optional.of(handleProtocolStepZero(baseProtocol));
        case TWO:
          return Optional.of(handleProtocolStepTwo(baseProtocol));
        case FOUR:
          return Optional.of(handleProtocolStepFour(baseProtocol));
        case SIX:
          return Optional.of(handleProtocolStepSix(baseProtocol));
        case ERROR:
          return Optional.empty();
        case ONE:
        case THREE:
        case FIVE:
        case SEVEN:
          return Optional.of(new BaseProtocolBuilder().setId(ProtocolId.ERROR)
              .setStatus(ProtocolStatus.UNEXPECTED_ID).createBaseProtocol());
        default:
          return Optional.of(new BaseProtocolBuilder().setId(ProtocolId.ERROR)
              .setStatus(ProtocolStatus.UNKNOWN_PROTOCOL).createBaseProtocol());
      }
    }

    return Optional.empty();
  }

  private BaseProtocol handleProtocolStepSix(BaseProtocol given) {

    List<BigInteger> privateParametersForKeyA = given.getPrivateParametersForKeyA();
    if (privateParametersForKeyA.isEmpty()) {
      return new BaseProtocolBuilder().setId(ProtocolId.SIX).setStatus(ProtocolStatus.NO_KEY)
          .createBaseProtocol();
    }

    // todo (13.12.2015): implement protocol verification with given key from client.

    KeyPair keyPair = CoinFlipServer.keyMap.get(sessionId);
    PrivateKeyParts privateParts;
    try {
      privateParts = KeyDataExtractor.getPrivateParts(keyPair);
    } catch (CipherException e) {
      return new BaseProtocolBuilder().setId(ProtocolId.SIX).setStatus(ProtocolStatus.EXCEPTION)
          .createBaseProtocol();
    }

    return new BaseProtocolBuilder().setId(ProtocolId.SEVEN).setStatus(ProtocolStatus.OK)
        .setChosenVersion(given.getNegotiatedVersion())
        .setProposedVersions(given.getProposedVersions()).setChosenSid(given.getSid())
        .setAvailableSids(given.getAvailableSids()).setInitialCoin(given.getPlainCoin())
        .setEncryptedCoin(given.getEncryptedCoin())
        .setDesiredCoin(given.getDesiredCoinSide()).setEnChosenCoin(given.getEncryptedChosenCoin())
        .setDeChosenCoin(given.getDecryptedChosenCoin())
        .setKeyA(given.getPrivateParametersForKeyA())
        .setKeyB(Lists.newArrayList(privateParts.getE(), privateParts.getD())).createBaseProtocol();
  }

  private BaseProtocol handleProtocolStepZero(BaseProtocol given) {
    List<Versions> proposed = given.getProposedVersions();

    if (proposed == null || proposed.isEmpty()) {
      return new BaseProtocolBuilder().setId(ProtocolId.ZERO).setStatus(ProtocolStatus.NO_VERSION)
          .createBaseProtocol();
    }

    List<Versions> newVersions = Lists.newArrayList(proposed);
    newVersions.add(CoinFlip.supportedVersions);

    Set<String> intersection =
        Sets.intersection(newVersions.get(0).get(), newVersions.get(1).get());

    if (intersection.isEmpty()) {
      return new BaseProtocolBuilder().setId(ProtocolId.ZERO).setStatus(ProtocolStatus.NO_VERSION)
          .setProposedVersions(given.getProposedVersions()).createBaseProtocol();
    }

    String chosenVersion = intersection.iterator().next();
    return new BaseProtocolBuilder().setId(ProtocolId.ONE).setStatus(ProtocolStatus.OK)
        .setProposedVersions(newVersions).setChosenVersion(chosenVersion).createBaseProtocol();
  }

  private BaseProtocol handleProtocolStepTwo(BaseProtocol given) {
    List<Sids> proposed = given.getAvailableSids();

    if (proposed == null || proposed.isEmpty()) {
      return new BaseProtocolBuilder().setId(ProtocolId.TWO).setStatus(ProtocolStatus.ERROR)
          .createBaseProtocol();
    }

    List<Sids> newSids = Lists.newArrayList(proposed);
    newSids.add(CoinFlip.supportedSids);

    Set<Sid> intersection = Sets.intersection(newSids.get(0).get(), newSids.get(1).get());

    if (intersection.isEmpty()) {
      return new BaseProtocolBuilder().setId(ProtocolId.TWO).setStatus(ProtocolStatus.ERROR)

      .setAvailableSids(given.getAvailableSids()).createBaseProtocol();
    }

    Sid chosenSid = intersection.iterator().next();

    KeyPair keyPair;
    PublicKeyParts parts;
    try {
      // generate the key pair.
      keyPair = KeyPairFactory.generateKeyPair(chosenSid.getModulus());
      // fetch public parts of the key-pair to publish them.
      parts = KeyDataExtractor.getPublicKeyParts(keyPair);
    } catch (CipherException e) {
      return new BaseProtocolBuilder().setId(ProtocolId.THREE).setStatus(ProtocolStatus.EXCEPTION)
          .createBaseProtocol();
    }

    // store the key pair with a link to this session.
    CoinFlipServer.keyMap.put(sessionId, keyPair);

    return new BaseProtocolBuilder().setChosenVersion(given.getNegotiatedVersion())
        .setProposedVersions(given.getProposedVersions()).setStatus(ProtocolStatus.OK)
        .setId(ProtocolId.THREE).setAvailableSids(newSids).setChosenSid(chosenSid)
        .setPublicKeyParts(parts.getP(), parts.getQ()).createBaseProtocol();
  }

  private BaseProtocol handleProtocolStepFour(BaseProtocol given) {
    List<String> encryptedCoin = given.getEncryptedCoin();

    if (encryptedCoin.size() != 2) {
      return new BaseProtocolBuilder().setId(ProtocolId.FOUR).setStatus(ProtocolStatus.ERROR)
          .createBaseProtocol();
    }

    List<String> chooseFrom = Lists.newArrayList(encryptedCoin);
    Collections.shuffle(chooseFrom);

    String chosenCoinSide = chooseFrom.get(0);

    // prepare the engine for encryption
    KeyPair keyPair = CoinFlipServer.keyMap.get(sessionId);

    String encryptedCoinSide;
    try {
      encryptedCoinSide =
          CryptoEngine.encrypt(Hex.decode(chosenCoinSide), "SRA", keyPair.getPublic());
    } catch (CipherException e) {
      return new BaseProtocolBuilder().setId(ProtocolId.FOUR).setStatus(ProtocolStatus.EXCEPTION)
          .createBaseProtocol();
    }

    List<String> plainCoin = given.getPlainCoin();

    if (plainCoin.size() != 2) {
      return new BaseProtocolBuilder().setId(ProtocolId.FOUR).setStatus(ProtocolStatus.ERROR)
          .createBaseProtocol();
    }

    List<String> coin = Lists.newArrayList(plainCoin);
    Collections.shuffle(coin);

    return new BaseProtocolBuilder().setId(ProtocolId.FIVE).setStatus(ProtocolStatus.OK)
        .setChosenVersion(given.getNegotiatedVersion())
        .setProposedVersions(given.getProposedVersions()).setAvailableSids(given.getAvailableSids())
        .setChosenSid(given.getSid()).setPublicKeyParts(given.getP(), given.getQ())
        .setInitialCoin(given.getPlainCoin()).setEncryptedCoin(given.getEncryptedCoin())
        .setEnChosenCoin(encryptedCoinSide).setDesiredCoin(coin.get(0)).createBaseProtocol();
  }
}
