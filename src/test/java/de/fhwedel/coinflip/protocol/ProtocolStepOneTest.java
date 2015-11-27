package de.fhwedel.coinflip.protocol;

import de.fhwedel.coinflip.protocol.id.ProtocolId;
import org.apache.commons.io.FileUtils;
import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProtocolStepOneTest {
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
    assertThat(baseProtocol.getStep()).isEqualTo(ProtocolId.ONE.getId());
  }

  @Test
  public void twoVersionProposalsAreAvailable() throws Exception {
    BaseProtocol baseProtocol = parser.parseJson(jsonString);
    List<Versions> proposedVersions = baseProtocol.getProposedVersions();

    assertThat(proposedVersions).hasSize(2);
    assertThat(proposedVersions).areExactly(2, foo);
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