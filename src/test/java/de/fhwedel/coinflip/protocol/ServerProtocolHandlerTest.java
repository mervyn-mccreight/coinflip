package de.fhwedel.coinflip.protocol;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.fhwedel.coinflip.CoinFlip;
import de.fhwedel.coinflip.protocol.model.BaseProtocol;
import de.fhwedel.coinflip.protocol.model.BaseProtocolBuilder;
import de.fhwedel.coinflip.protocol.model.Sids;
import de.fhwedel.coinflip.protocol.model.Versions;
import de.fhwedel.coinflip.protocol.model.id.ProtocolId;
import de.fhwedel.coinflip.protocol.model.sid.Sid;
import de.fhwedel.coinflip.protocol.model.status.ProtocolStatus;

public class ServerProtocolHandlerTest {

  public static final int SESSION_ID = 1;
  private ProtocolHandler handler;

  @Before
  public void setUp() throws Exception {
    handler = new ServerProtocolHandler(SESSION_ID);
  }

  @Test
  public void validStepZero_noError_returnsValidStepOne() throws Exception {
    List<Versions> versions = Lists.newArrayList(Versions.containing("1.0"));
    BaseProtocol input = new BaseProtocolBuilder().setId(ProtocolId.ZERO)
        .setStatus(ProtocolStatus.OK).setProposedVersions(versions).createBaseProtocol();

    Optional<BaseProtocol> output = handler.work(Optional.of(input));

    assertThat(output.isPresent()).isTrue();

    BaseProtocol realOutput = output.get();

    assertThat(realOutput.getId()).isEqualTo(ProtocolId.ONE);

    Versions supportedVersions = CoinFlip.supportedVersions;

    Set<String> intersection = Sets.intersection(supportedVersions.get(), versions.get(0).get());
    assertThat(realOutput.getNegotiatedVersion()).isIn(intersection);

    assertThat(realOutput.getProposedVersions()).containsOnly(Versions.containing("1.0"),
        CoinFlip.supportedVersions);

    assertThat(realOutput.getStatus()).isEqualTo(ProtocolStatus.OK.getId());
    assertThat(realOutput.getStatusMessage()).isEqualTo(ProtocolStatus.OK.getMessage());
  }

  @Test
  @Ignore("only works with open-jdk, since i use jce with an unsigned bc-jar.")
  public void validStepTwo_noError_returnsValidStepThree() throws Exception {
    BaseProtocol input = new BaseProtocolBuilder().setId(ProtocolId.TWO)
        .setStatus(ProtocolStatus.OK).setChosenVersion("1.0")
        .setProposedVersions(
            Lists.newArrayList(Versions.containing("1.0"), Versions.containing("1.0")))
        .setAvailableSids(
            Lists.newArrayList(Sids.containing(Sid.SRA1024SHA1, Sid.SRA2048SHA1, Sid.SRA3072SHA1)))
        .createBaseProtocol();

    Optional<BaseProtocol> maybeOutput = handler.work(Optional.of(input));

    assertThat(maybeOutput.isPresent()).isTrue();
    BaseProtocol output = maybeOutput.get();

    assertThat(output.getId()).isEqualTo(ProtocolId.THREE);
    assertThat(output.getStatus()).isEqualTo(ProtocolStatus.OK.getId());

    // todo (15.12.2015): implement further with open-jdk or signed jar.
  }

  @Test
  @Ignore("only works with open-jdk, since i use jce with an unsigned bc-jar.")
  public void validStepFour_noError_returnsValidStepFive() throws Exception {
    // todo (15.12.2015): implement further with open-jdk or signed jar.
  }

  @Test
  @Ignore("only works with open-jdk, since i use jce with an unsigned bc-jar.")
  public void validStepSix_noError_returnsValidStepSeven() throws Exception {
    // todo (15.12.2015): implement further with open-jdk or signed jar.
  }

  // todo (15.12.2015): test error cases!

  // todo (15.12.2015): write test for https://github.com/mervyn-mccreight/coinflip/issues/1.
  // fix the problem.
}
