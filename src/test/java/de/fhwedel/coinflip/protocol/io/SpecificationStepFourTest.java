package de.fhwedel.coinflip.protocol.io;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import de.fhwedel.coinflip.protocol.model.BaseProtocol;
import de.fhwedel.coinflip.protocol.model.id.ProtocolId;

public class SpecificationStepFourTest extends AbstractProtocolSpecificationTest {
  private BaseProtocol protocol;

  private static final String encryptedSideOne =
      "0d3ad3e76879672db04ee479045ccd50d9eea72412a3c7621fc1b543d6ac33d34fc39e7f00741c68184c5dadd8695b1ba38c7d162787f4ab9bc23d428ffdb99084a00fe4e83676806fd5fa05ad3abc1845ca55cada2f760713d22282b135655e113174720a494212cc8346ff2635ccbc19874c8d00e70cb1427c0b3d98dfea3d5ba1706a2a9eb9be72647ce2620f28f19abf04d5ee6de7d0367f5f22f411381922c5a98f10358103b2990416885a7abb71ecfdf66609110f4fca727abddfb9b11c0e91ab2e23b957bbebd58d41a600742bf294575962edeca9452fce7b8b26ce9c49975396c329b04e04a3e73d6c13b54b41bc39a2e300bb6cf8be0cbf602245";
  private static final String encryptedSideTwo =
      "04ccb2e960cc43757a82cf29b8e8112550432aa3b816a00cbed5cc7af14ca2293fb1175b5b34db1a11b1a8eb4822c754837ce19aef131ef9abaa92beaadf2e5e8d1b8b8ce63c0f5b298b76ff21dea9ac54b1811b9e66044c8103b4248eb4dc20355941f8710024c518b16a60898aed5a955f2caa88d2c141a3745875bff6af3af3ac60be2c4493e0ab5086f7689a8d42fbb7813b078b03615ff6b4aec34399dccf182257cb6c18f002e91d4b45dea70dd3f4697d537544fd31c37c54a90415aa5ba2c490d3810d8708c4e2a0f378a0f7123505f008c48deb48ae739972d822ca7a10caf4e632f62cab628d75462d6b07149218ebe74ef20dde343cc56ee103cd";

  @Before
  public void setUp() throws Exception {
    // given
    File file = new File("src/test/resources/protocol/specification/4.json");
    String jsonString = FileUtils.readFileToString(file);
    ProtocolParser parser = new ProtocolParser();
    protocol = parser.parseJson(jsonString).get();
  }

  @Test
  public void step() throws Exception {
    assertProtocolStep(protocol, ProtocolId.FOUR);
  }

  @Test
  public void initialCoinIsSet() throws Exception {
    assertThat(protocol.getPlainCoin()).containsExactly("HEAD", "TAIL");
  }

  @Test
  public void desiredCoinIsNotSet() throws Exception {
    assertThat(protocol.getDesiredCoinSide()).isNull();
  }

  @Test
  public void encryptedCoinIsSet() throws Exception {
    assertThat(protocol.getEncryptedCoin()).containsExactly(encryptedSideOne, encryptedSideTwo);
  }

  @Test
  public void noEncryptedChosenCoinSide() throws Exception {
    assertThat(protocol.getEncryptedChosenCoin()).isNull();
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
