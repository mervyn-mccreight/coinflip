package de.fhwedel.coinflip.protocol;

import java.util.Optional;

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
    // todo (16.12.2015): implement
    return new BaseProtocolBuilder().setId(ProtocolId.ERROR).setStatus(ProtocolStatus.EXCEPTION)
        .createBaseProtocol();
  }
}
