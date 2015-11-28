package de.fhwedel.coinflip.protocol;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Test;

import de.fhwedel.coinflip.protocol.io.ProtocolParser;
import de.fhwedel.coinflip.protocol.model.BaseProtocol;
import de.fhwedel.coinflip.protocol.model.Versions;
import de.fhwedel.coinflip.protocol.model.id.ProtocolId;

public class SpecificationStepOneTest extends AbstractProtocolSpecificationTest {
  private String jsonString;
  private ProtocolParser parser;

  @Before
  public void setUp() throws Exception {
    // given
    File file = new File("src/test/resources/protocol/specification/1.json");
    jsonString = FileUtils.readFileToString(file);
    parser = new ProtocolParser();
  }

  @Test
  public void getStep() throws Exception {
    BaseProtocol baseProtocol = parser.parseJson(jsonString);
    assertProtocolStep(baseProtocol, ProtocolId.ONE);
  }

  @Test
  public void twoVersionProposalsAreAvailable() throws Exception {
    BaseProtocol baseProtocol = parser.parseJson(jsonString);
    List<Versions> proposedVersions = baseProtocol.getProposedVersions();

    assertThat(proposedVersions).hasSize(2);
    assertThat(proposedVersions).areExactly(2, foo);
  }

  @Test
  public void versionIsSet() throws Exception {
    BaseProtocol baseProtocol = parser.parseJson(jsonString);
    assertNegotiatedVersionIsSetTo(baseProtocol, "1.0");
  }

  private final Condition<Versions> foo = new Condition<Versions>("only version 1.0") {
    @Override
    public boolean matches(Versions value) {
      List<String> strings = value.get();
      boolean size = strings.size() == 1;
      String s = strings.get(0);
      boolean content = s.equals("1.0");
      return size && content;
    }
  };
}
