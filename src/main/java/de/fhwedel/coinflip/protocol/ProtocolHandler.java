package de.fhwedel.coinflip.protocol;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.fhwedel.coinflip.CoinFlip;
import de.fhwedel.coinflip.protocol.model.BaseProtocol;
import de.fhwedel.coinflip.protocol.model.BaseProtocolBuilder;
import de.fhwedel.coinflip.protocol.model.Versions;
import de.fhwedel.coinflip.protocol.model.id.ProtocolId;
import de.fhwedel.coinflip.protocol.model.status.ProtocolStatus;

public class ProtocolHandler {
  public static Optional<BaseProtocol> work(Optional<BaseProtocol> protocol) {

    if (protocol.isPresent()) {
      BaseProtocol baseProtocol = protocol.get();
      switch (baseProtocol.getId()) {
        case ZERO:
          return Optional.of(handleProtocolStepZero(baseProtocol));
        case ONE:
          break;
        case TWO:
          break;
        case THREE:
          break;
        case FOUR:
          break;
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

  private static BaseProtocol handleProtocolStepZero(BaseProtocol given) {
    List<Versions> proposed = given.getProposedVersions();

    if (proposed.isEmpty() || proposed == null) {
      return new BaseProtocolBuilder().setId(ProtocolId.ERROR).setStatus(ProtocolStatus.ERROR)
          .setStatusMessage(ProtocolStatus.ERROR.getMessage()).createBaseProtocol();
    }

    List<Versions> newVersions = Lists.newArrayList(proposed);
    newVersions.add(CoinFlip.supportedVersions);

    Sets.SetView<String> intersection =
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
}
