package de.fhwedel.coinflip.protocol;

import java.util.Optional;

import com.google.common.collect.Lists;

import de.fhwedel.coinflip.CoinFlip;
import de.fhwedel.coinflip.protocol.model.BaseProtocol;
import de.fhwedel.coinflip.protocol.model.BaseProtocolBuilder;
import de.fhwedel.coinflip.protocol.model.id.ProtocolId;
import de.fhwedel.coinflip.protocol.model.status.ProtocolStatus;

public class ClientProtocolHandler implements ProtocolHandler {
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
          break;

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
