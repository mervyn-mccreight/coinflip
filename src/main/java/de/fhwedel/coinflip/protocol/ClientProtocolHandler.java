package de.fhwedel.coinflip.protocol;

import java.util.Optional;

import de.fhwedel.coinflip.protocol.model.BaseProtocol;

public class ClientProtocolHandler implements ProtocolHandler {
  @Override
  public Optional<BaseProtocol> work(Optional<BaseProtocol> protocol) {
    return Optional.empty();
  }
}
