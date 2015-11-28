package de.fhwedel.coinflip.protocol;

import static org.assertj.core.api.Assertions.assertThat;

import de.fhwedel.coinflip.protocol.model.BaseProtocol;
import de.fhwedel.coinflip.protocol.model.id.ProtocolId;

public abstract class AbstractProtocolSpecificationTest {
  public void assertEmptyPayload(BaseProtocol protocol) {
    assertThat(protocol.getPlainCoin()).isEmpty();
    assertThat(protocol.getDesiredCoinSide()).isNull();
    assertThat(protocol.getEncryptedCoin()).isEmpty();
    assertThat(protocol.getEncryptedChosenCoin()).isNull();
    assertThat(protocol.getDecryptedChosenCoin()).isNull();
    assertThat(protocol.getPrivateParametersForKeyA()).isEmpty();
    assertThat(protocol.getPrivateParametersForKeyB()).isEmpty();
  }

  public void assertProtocolStep(BaseProtocol protocol, ProtocolId expected) {
    assertThat(protocol.getStep()).isEqualTo(expected.getId());
  }
}
