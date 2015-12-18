package de.fhwedel.coinflip.protocol;

import java.security.KeyPair;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;

import de.fhwedel.coinflip.CoinFlip;
import de.fhwedel.coinflip.CoinFlipClient;
import de.fhwedel.coinflip.cipher.CryptoEngine;
import de.fhwedel.coinflip.cipher.KeyPairFactory;
import de.fhwedel.coinflip.cipher.exception.CipherException;
import de.fhwedel.coinflip.protocol.model.BaseProtocol;
import de.fhwedel.coinflip.protocol.model.BaseProtocolBuilder;
import de.fhwedel.coinflip.protocol.model.id.ProtocolId;
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
          break;

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

  private BaseProtocol handleProtocolStepThree(BaseProtocol given) {
    if (!CoinFlip.supportedSids.get().contains(given.getSid())) {
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
          KeyPairFactory.generateKeyPair(given.getSid().getModulus(), given.getP(), given.getQ());
      encryptedCoin = Lists.newArrayList(CoinFlipClient.coin);
      Collections.shuffle(encryptedCoin);

      encryptedCoin.replaceAll(x -> {
        try {
          return CryptoEngine.encrypt(x.getBytes(), given.getSid().getAlgorithm(),
              keyPair.getPublic());
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
        .setPublicKeyParts(given.getP(), given.getQ()).setChosenSid(given.getSid())
        .setAvailableSids(given.getAvailableSids())
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