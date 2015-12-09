package de.fhwedel.coinflip.protocol;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import de.fhwedel.coinflip.protocol.io.ProtocolParser;
import de.fhwedel.coinflip.protocol.model.BaseProtocol;
import de.fhwedel.coinflip.protocol.model.id.ProtocolId;

public class SpecificationStepFiveTest extends AbstractProtocolSpecificationTest {
  private BaseProtocol protocol;

  private static final String expectedEncryptedCoinSide =
      "95a9c24f541b5cf049e39b3168ca7712ac0e0c900de7c8723e1cdb905c3eef75288c02dc922d2e6d81e14be8ebd1ec196c037682604848b1946895746bb6b456dcf0f301a419cd0b5abe22efd72c39acd055c41f6c23ac73cc30e04dba17c721b7d552e7e15fa6bb5602d5147bcb9bc463b06d5c10036e0d2b085f80e5b1ca29e71d739143005c58b1becb7cebe83f844b1aae3aec0a3e66e1717debf49c33003d4b1a2341f8db7887b6a67002a8e475230da212e1f8d9e5c86762086c5ebf8165414401d335fd3a47915e4a9b53e6390b2c311b84b9ba842aba7b2610ec17c7fe9d325ceb7ade960061d6c85fb85f94f0961f116fa816e2851d532244f0ca8d";

  @Before
  public void setUp() throws Exception {
    // given
    File file = new File("src/test/resources/protocol/specification/5.json");
    String jsonString = FileUtils.readFileToString(file);
    ProtocolParser parser = new ProtocolParser();
    protocol = parser.parseJson(jsonString).get();
  }

  @Test
  public void step() throws Exception {
    assertProtocolStep(protocol, ProtocolId.FIVE);
  }

  @Test
  public void desiredCoinIsNotSet() throws Exception {
    assertThat(protocol.getDesiredCoinSide()).isEqualTo("TAIL");
  }

  @Test
  public void noEncryptedChosenCoinSide() throws Exception {
    assertThat(protocol.getEncryptedChosenCoin()).isEqualTo(expectedEncryptedCoinSide);
  }

  @Test
  public void noDecryptedChosenCoinSide() throws Exception {
    assertThat(protocol.getDecryptedChosenCoin()).isNull();
  }

  @Test
  public void noPrivateKeyParts() throws Exception {
    assertThat(protocol.getPrivateParametersForKeyA()).isEmpty();
    assertThat(protocol.getPrivateParametersForKeyB()).isEmpty();
  }
}
