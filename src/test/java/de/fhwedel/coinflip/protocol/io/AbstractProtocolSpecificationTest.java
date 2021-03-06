package de.fhwedel.coinflip.protocol.io;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;

import de.fhwedel.coinflip.protocol.model.BaseProtocol;
import de.fhwedel.coinflip.protocol.model.id.ProtocolId;
import de.fhwedel.coinflip.protocol.model.sid.Sid;
import de.fhwedel.coinflip.protocol.model.status.ProtocolStatus;

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

  public void assertNoNegotiatedVersion(BaseProtocol protocol) {
    assertThat(protocol.getNegotiatedVersion()).isNull();
  }

  public void assertNegotiatedVersionIsSetTo(BaseProtocol protocol, String version) {
    assertThat(protocol.getNegotiatedVersion()).isEqualTo(version);
  }

  public void assertEmptyPandQ(BaseProtocol protocol) {
    assertThat(protocol.getP()).isNull();
    assertThat(protocol.getQ()).isNull();
  }

  public void assertSid(BaseProtocol protocol, Sid sid) {
    assertThat(protocol.getSidId()).isEqualTo(sid.getId());
  }

  public void assertStatusId(BaseProtocol protocol, ProtocolStatus status) {
    assertThat(protocol.getStatus()).isEqualTo(status.getId());
  }

  public void assertPandQ(BaseProtocol protocol, BigInteger p, BigInteger q) {
    assertThat(protocol.getP()).isEqualTo(p);
    assertThat(protocol.getQ()).isEqualTo(q);
  }
}
