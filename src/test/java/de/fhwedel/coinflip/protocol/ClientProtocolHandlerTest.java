package de.fhwedel.coinflip.protocol;

import static org.assertj.core.api.Assertions.assertThat;

import java.security.Security;
import java.util.Optional;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import de.fhwedel.coinflip.CoinFlip;
import de.fhwedel.coinflip.protocol.model.BaseProtocol;
import de.fhwedel.coinflip.protocol.model.BaseProtocolBuilder;
import de.fhwedel.coinflip.protocol.model.Versions;
import de.fhwedel.coinflip.protocol.model.id.ProtocolId;
import de.fhwedel.coinflip.protocol.model.status.ProtocolStatus;


public class ClientProtocolHandlerTest {

  private ClientProtocolHandler handler;

  @Before
  public void setUp() throws Exception {
    Security.addProvider(new BouncyCastleProvider());
    handler = new ClientProtocolHandler();
  }

  @Test
  public void validStepOne_noError_returnValidStepTwo() throws Exception {
    BaseProtocol input = new BaseProtocolBuilder().setId(ProtocolId.ONE)
        .setStatus(ProtocolStatus.OK).setChosenVersion("1.0")
        .setProposedVersions(
            Lists.newArrayList(Versions.containing("1.0"), Versions.containing("1.0")))
        .createBaseProtocol();

    Optional<BaseProtocol> maybeOutput = handler.work(Optional.of(input));

    assertThat(maybeOutput.isPresent()).isTrue();

    BaseProtocol output = maybeOutput.get();

    assertThat(output.getId()).isEqualTo(ProtocolId.TWO);
    assertThat(output.getStatus()).isEqualTo(ProtocolStatus.OK.getId());

    assertThat(output.getAvailableSidsIds()).hasSize(1);
    assertThat(output.getAvailableSidsIds().get(0)).isEqualTo(CoinFlip.supportedSids);
  }
}
