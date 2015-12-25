package de.fhwedel.coinflip.protocol;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.security.Security;
import java.util.Optional;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.fhwedel.coinflip.CoinFlip;
import de.fhwedel.coinflip.CoinFlipServer;
import de.fhwedel.coinflip.cipher.KeyPairFactory;
import de.fhwedel.coinflip.protocol.model.BaseProtocol;
import de.fhwedel.coinflip.protocol.model.BaseProtocolBuilder;
import de.fhwedel.coinflip.protocol.model.Sids;
import de.fhwedel.coinflip.protocol.model.Versions;
import de.fhwedel.coinflip.protocol.model.id.ProtocolId;
import de.fhwedel.coinflip.protocol.model.status.ProtocolStatus;


public class ClientProtocolHandlerTest {

  private ClientProtocolHandler handler;
  private final BigInteger p = new BigInteger(
      "10995358201443879357334787886580507605169973785691610407989111943364935788375084145338442298447111808008676495721625148502168361464958829567069968510172553");
  private final BigInteger q = new BigInteger(
      "10787650629861197120068211907670059598710320442594233097194402844151901446987493760059663605015514852230661342851623589545588993259030700313675582311433353");

  @Before
  public void setUp() throws Exception {
    Security.addProvider(new BouncyCastleProvider());
    handler = new ClientProtocolHandler();
    CoinFlipServer.keyMap.put(1, KeyPairFactory.generateKeyPair(1024, p, q));
  }

  @After
  public void tearDown() throws Exception {
    CoinFlipServer.keyMap.remove(1);
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

  private boolean isHexadecimal(String possibleHex) {
    return possibleHex.matches("-?[0-9a-fA-F]+");
  }

  @Test
  public void validStepThree_noError_returnValidStepFour() throws Exception {
    BaseProtocol input = new BaseProtocolBuilder().setId(ProtocolId.THREE)
        .setStatus(ProtocolStatus.OK).setChosenVersion("1.0")
        .setProposedVersions(
            Lists.newArrayList(Versions.containing("1.0"), Versions.containing("1.0")))
        .setChosenSid(0).setAvailableSids(Lists.newArrayList(new Sids(Sets.newHashSet(0, 1, 2)),
            new Sids(Sets.newHashSet(0, 1, 2))))
        .setPublicKeyParts(p, q).createBaseProtocol();

    Optional<BaseProtocol> maybeOutput = handler.work(Optional.of(input));

    assertThat(maybeOutput.isPresent()).isTrue();

    BaseProtocol output = maybeOutput.get();

    assertThat(output.getId()).isEqualTo(ProtocolId.FOUR);
    assertThat(output.getStatus()).isEqualTo(ProtocolStatus.OK.getId());

    assertThat(output.getPlainCoin()).containsOnly("HEAD", "TAIL");
    assertThat(output.getEncryptedCoin()).hasSize(2);

    assertThat(isHexadecimal(output.getEncryptedCoin().get(0))).isTrue();
    assertThat(isHexadecimal(output.getEncryptedCoin().get(1))).isTrue();
  }

  @Test
  public void validStepFive_noError_returnValidStepSix() throws Exception {
    // workaround, because client needs a key-pair, which is
    // generated in handling step three.
    BaseProtocol input = new BaseProtocolBuilder().setId(ProtocolId.THREE)
        .setStatus(ProtocolStatus.OK).setChosenVersion("1.0")
        .setProposedVersions(
            Lists.newArrayList(Versions.containing("1.0"), Versions.containing("1.0")))
        .setChosenSid(0).setAvailableSids(Lists.newArrayList(new Sids(Sets.newHashSet(0, 1, 2)),
            new Sids(Sets.newHashSet(0, 1, 2))))
        .setPublicKeyParts(p, q).createBaseProtocol();

    Optional<BaseProtocol> maybeStepFour = handler.work(Optional.of(input));

    ServerProtocolHandler serverProtocolHandler = new ServerProtocolHandler(1);
    Optional<BaseProtocol> maybeStepFive = serverProtocolHandler.work(maybeStepFour);

    Optional<BaseProtocol> maybeOutput = handler.work(maybeStepFive);

    assertThat(maybeOutput.isPresent()).isTrue();

    BaseProtocol output = maybeOutput.get();

    assertThat(output.getId()).isEqualTo(ProtocolId.SIX);
    assertThat(output.getStatus()).isEqualTo(ProtocolStatus.OK.getId());

    assertThat(isHexadecimal(output.getDecryptedChosenCoin())).isTrue();
  }
}
