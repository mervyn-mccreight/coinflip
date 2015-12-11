package de.fhwedel.coinflip.protocol;

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
import de.fhwedel.coinflip.cipher.CryptoEngine;
import de.fhwedel.coinflip.cipher.KeyDataExtractor;
import de.fhwedel.coinflip.cipher.KeyPairFactory;
import de.fhwedel.coinflip.cipher.PublicKeyParts;
import de.fhwedel.coinflip.cipher.exception.CipherException;
import de.fhwedel.coinflip.protocol.model.BaseProtocol;
import de.fhwedel.coinflip.protocol.model.BaseProtocolBuilder;
import de.fhwedel.coinflip.protocol.model.Sids;
import de.fhwedel.coinflip.protocol.model.Versions;
import de.fhwedel.coinflip.protocol.model.id.ProtocolId;
import de.fhwedel.coinflip.protocol.model.sid.Sid;
import de.fhwedel.coinflip.protocol.model.status.ProtocolStatus;

public class ProtocolHandler {

  private int sessionId;

  public ProtocolHandler(int sessionId) {
    this.sessionId = sessionId;
  }

  public Optional<BaseProtocol> work(Optional<BaseProtocol> protocol) {
    if (protocol.isPresent()) {
      BaseProtocol baseProtocol = protocol.get();
      switch (baseProtocol.getId()) {
        case ZERO:
          return Optional.ofNullable(handleProtocolStepZero(baseProtocol));
        case ONE:
          break;
        case TWO:
          return Optional.ofNullable(handleProtocolStepTwo(baseProtocol));
        case THREE:
          break;
        case FOUR:
          return Optional.ofNullable(handleProtocolStepFour(baseProtocol));
        case FIVE:
          break;
        case SIX:
          break;
        case SEVEN:
          break;
        case ERROR:
          break;
      }
    }

    return Optional.empty();
  }

  private BaseProtocol handleProtocolStepZero(BaseProtocol given) {
    List<Versions> proposed = given.getProposedVersions();

    if (proposed.isEmpty() || proposed == null) {
      return new BaseProtocolBuilder().setId(ProtocolId.ZERO).setStatus(ProtocolStatus.NO_VERSION)
          .setStatusMessage(ProtocolStatus.NO_VERSION.getMessage()).createBaseProtocol();
    }

    List<Versions> newVersions = Lists.newArrayList(proposed);
    newVersions.add(CoinFlip.supportedVersions);

    Set<String> intersection =
        Sets.intersection(newVersions.get(0).get(), newVersions.get(1).get());

    if (intersection.isEmpty()) {
      return new BaseProtocolBuilder().setId(ProtocolId.ZERO).setStatus(ProtocolStatus.NO_VERSION)
          .setStatusMessage(ProtocolStatus.NO_VERSION.getMessage())
          .setProposedVersions(given.getProposedVersions()).createBaseProtocol();
    }

    BaseProtocolBuilder builder = new BaseProtocolBuilder();

    builder.setId(ProtocolId.ONE);
    builder.setStatusMessage(ProtocolStatus.OK.getMessage());
    builder.setStatus(ProtocolStatus.OK);
    builder.setProposedVersions(newVersions);
    builder.setChosenVersion(intersection.iterator().next());
    return builder.createBaseProtocol();
  }

  private BaseProtocol handleProtocolStepTwo(BaseProtocol given) {
    List<Sids> proposed = given.getAvailableSids();

    if (proposed.isEmpty() || proposed == null) {
      return new BaseProtocolBuilder().setId(ProtocolId.TWO).setStatus(ProtocolStatus.ERROR)
          .setStatusMessage(ProtocolStatus.ERROR.getMessage()).createBaseProtocol();
    }

    List<Sids> newSids = Lists.newArrayList(proposed);
    newSids.add(CoinFlip.supportedSids);

    Set<Sid> intersection = Sets.intersection(newSids.get(0).get(), newSids.get(1).get());

    if (intersection.isEmpty()) {
      return new BaseProtocolBuilder().setId(ProtocolId.TWO).setStatus(ProtocolStatus.ERROR)
          .setStatusMessage(ProtocolStatus.ERROR.getMessage())
          .setAvailableSids(given.getAvailableSids()).createBaseProtocol();
    }

    BaseProtocolBuilder builder = new BaseProtocolBuilder();

    builder.setChosenVersion(given.getNegotiatedVersion());
    builder.setProposedVersions(given.getProposedVersions());
    builder.setStatusMessage(ProtocolStatus.OK.getMessage());
    builder.setStatus(ProtocolStatus.OK);
    builder.setId(ProtocolId.THREE);

    builder.setAvailableSids(newSids);
    Sid chosenSid = intersection.iterator().next();
    builder.setChosenSid(chosenSid);

    // generate the key pair.
    KeyPair keyPair;
    try {
      keyPair = KeyPairFactory.generateKeyPair(chosenSid.getModulus());
    } catch (CipherException e) {
      return new BaseProtocolBuilder().setId(ProtocolId.FOUR).setStatus(ProtocolStatus.EXCEPTION)
          .setStatusMessage(ProtocolStatus.EXCEPTION.getMessage()).createBaseProtocol();
    }

    // store the key pair with a link to this session.
    CoinFlipServer.keyMap.put(sessionId, keyPair);

    PublicKeyParts parts;
    try {
      parts = KeyDataExtractor.getPublicKeyParts(keyPair);
    } catch (CipherException e) {
      return new BaseProtocolBuilder().setId(ProtocolId.FOUR).setStatus(ProtocolStatus.EXCEPTION)
          .setStatusMessage(ProtocolStatus.EXCEPTION.getMessage()).createBaseProtocol();
    }
    builder.setPublicKeyParts(parts.getP(), parts.getQ());
    return builder.createBaseProtocol();
  }

  private BaseProtocol handleProtocolStepFour(BaseProtocol given) {
    List<String> encryptedCoin = given.getEncryptedCoin();

    if (encryptedCoin.size() != 2) {
      return new BaseProtocolBuilder().setId(ProtocolId.FOUR).setStatus(ProtocolStatus.ERROR)
          .setStatusMessage(ProtocolStatus.ERROR.getMessage()).createBaseProtocol();
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
          .setStatusMessage(ProtocolStatus.EXCEPTION.getMessage()).createBaseProtocol();
    }

    BaseProtocolBuilder builder = new BaseProtocolBuilder();

    builder.setId(ProtocolId.FIVE);
    builder.setStatus(ProtocolStatus.OK);
    builder.setStatusMessage(ProtocolStatus.OK.getMessage());

    builder.setChosenVersion(given.getNegotiatedVersion());
    builder.setProposedVersions(given.getProposedVersions());

    builder.setAvailableSids(given.getAvailableSids());
    builder.setChosenSid(given.getSid());
    builder.setPublicKeyParts(given.getP(), given.getQ());

    builder.setInitialCoin(given.getPlainCoin());
    builder.setEncryptedCoin(given.getEncryptedCoin());

    builder.setEnChosenCoin(encryptedCoinSide);

    List<String> plainCoin = given.getPlainCoin();

    if (plainCoin.size() != 2) {
      return new BaseProtocolBuilder().setId(ProtocolId.FOUR).setStatus(ProtocolStatus.ERROR)
          .setStatusMessage(ProtocolStatus.ERROR.getMessage()).createBaseProtocol();
    }

    List<String> coin = Lists.newArrayList(plainCoin);
    Collections.shuffle(coin);

    builder.setDesiredCoin(coin.get(0));

    return builder.createBaseProtocol();
  }
}
