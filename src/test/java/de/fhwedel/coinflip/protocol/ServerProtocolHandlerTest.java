package de.fhwedel.coinflip.protocol;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.fhwedel.coinflip.CoinFlip;
import de.fhwedel.coinflip.CoinFlipServer;
import de.fhwedel.coinflip.cipher.CryptoEngine;
import de.fhwedel.coinflip.cipher.KeyPairFactory;
import de.fhwedel.coinflip.protocol.model.BaseProtocol;
import de.fhwedel.coinflip.protocol.model.BaseProtocolBuilder;
import de.fhwedel.coinflip.protocol.model.Sids;
import de.fhwedel.coinflip.protocol.model.Versions;
import de.fhwedel.coinflip.protocol.model.id.ProtocolId;
import de.fhwedel.coinflip.protocol.model.sid.Sid;
import de.fhwedel.coinflip.protocol.model.status.ProtocolStatus;

public class ServerProtocolHandlerTest {

  private static final int SESSION_ID = 1;
  private ProtocolHandler handler;
  private final BigInteger p = new BigInteger(
      "10995358201443879357334787886580507605169973785691610407989111943364935788375084145338442298447111808008676495721625148502168361464958829567069968510172553");
  private final BigInteger q = new BigInteger(
      "10787650629861197120068211907670059598710320442594233097194402844151901446987493760059663605015514852230661342851623589545588993259030700313675582311433353");

  @Before
  public void setUp() throws Exception {
    Security.addProvider(new BouncyCastleProvider());
    handler = new ServerProtocolHandler(SESSION_ID);

    // simulate an entry in the coinflipserver key to session-id map.
    CoinFlipServer.keyMap.put(SESSION_ID, KeyPairFactory.generateKeyPair(1024, p, q));
  }

  @After
  public void tearDown() throws Exception {
    CoinFlipServer.keyMap.remove(SESSION_ID);
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

    // todo (12/24/15): consider checking for prime.
    assertThat(output.getP()).isNotNull();
    assertThat(output.getQ()).isNotNull();
    assertThat(output.getQ()).isNotEqualTo(output.getP());

    assertThat(output.getAvailableSids()).hasSize(2);
    Sids own = output.getAvailableSids().get(1);

    assertThat(own).isEqualTo(CoinFlip.supportedSids);
    assertThat(output.getSid()).isIn(CoinFlip.supportedSids.get());
  }

  @Test
  public void validStepFour_noError_returnsValidStepFive() throws Exception {
    ArrayList<String> coin = Lists.newArrayList("HEAD", "TAIL");

    KeyPair keyPair = KeyPairFactory.generateKeyPair(1024, p, q);
    String headEncryption = CryptoEngine.encrypt("HEAD".getBytes(), Sid.SRA1024SHA1.getAlgorithm(),
        keyPair.getPublic());
    String tailEncryption = CryptoEngine.encrypt("TAIL".getBytes(), Sid.SRA1024SHA1.getAlgorithm(),
        keyPair.getPublic());

    BaseProtocol input = new BaseProtocolBuilder().setId(ProtocolId.FOUR)
        .setStatus(ProtocolStatus.OK).setChosenVersion("1.0")
        .setProposedVersions(
            Lists.newArrayList(CoinFlip.supportedVersions, CoinFlip.supportedVersions))
        .setChosenSid(Sid.SRA1024SHA1)
        .setAvailableSids(Lists.newArrayList(CoinFlip.supportedSids, CoinFlip.supportedSids))
        .setPublicKeyParts(p, q).setInitialCoin(coin)
        .setEncryptedCoin(Lists.newArrayList(headEncryption, tailEncryption)).createBaseProtocol();

    Optional<BaseProtocol> maybeOutput = handler.work(Optional.of(input));

    assertThat(maybeOutput.isPresent()).isTrue();

    BaseProtocol output = maybeOutput.get();

    assertThat(output.getId()).isEqualTo(ProtocolId.FIVE);
    assertThat(output.getStatus()).isEqualTo(ProtocolStatus.OK.getId());

    assertThat(output.getEncryptedChosenCoin()).isNotNull();
    assertThat(output.getEncryptedChosenCoin()).isNotEmpty();
    assertThat(output.getDesiredCoinSide()).isIn("HEAD", "TAIL");

    assertThat(isHexadecimal(output.getEncryptedChosenCoin()));
  }

  private boolean isHexadecimal(String possibleHex) {
    return possibleHex.matches("-?[0-9a-fA-F]+");
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
