package de.fhwedel.coinflip.protocol;

import java.util.Optional;

import de.fhwedel.coinflip.protocol.model.BaseProtocol;

public interface ProtocolHandler {
  Optional<BaseProtocol> work(Optional<BaseProtocol> protocol);
}
