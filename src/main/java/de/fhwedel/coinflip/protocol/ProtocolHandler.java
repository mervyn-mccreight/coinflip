package de.fhwedel.coinflip.protocol;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Sets;
import com.sun.mail.iap.Protocol;
import de.fhwedel.coinflip.CoinFlip;
import de.fhwedel.coinflip.protocol.model.BaseProtocol;
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

  // todo (10.12.2015): move logic to base protocol class: BaseProtocol#createStepOne(ownVersions).
  // returns an error if the intersection is empty, like here.
  // otherwise creates a NEW INSTANCE (do not touch the given then!) with new values.
  private static BaseProtocol handleProtocolStepZero(BaseProtocol given) {
    given.addProposedVersions(CoinFlip.supportedVersions);
    List<Versions> proposedVersions = given.getProposedVersions();

    Set<String> intersection =
        Sets.intersection(proposedVersions.get(0).get(), proposedVersions.get(1).get());

    if (intersection.isEmpty()) {
      return new BaseProtocol(ProtocolId.ZERO, ProtocolStatus.NO_VERSION,
          ProtocolStatus.NO_VERSION.getMessage(), given.getProtocolNegotiation(), null);
    }

    String version = intersection.iterator().next();

    given.setChosenVersion(version);
    given.setProtocolId(ProtocolId.ONE);

    return given;
  }
}
