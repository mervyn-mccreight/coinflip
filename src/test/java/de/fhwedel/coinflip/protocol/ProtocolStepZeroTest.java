package de.fhwedel.coinflip.protocol;

import de.fhwedel.coinflip.protocol.status.ProtocolStatus;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class ProtocolStepZeroTest {
  private String jsonString;
  private ProtocolParser parser;

  @Before
  public void setUp() throws Exception {
    // given
    File file = new File("src/test/resources/0.json");
    jsonString = FileUtils.readFileToString(file);
    parser = new ProtocolParser();
  }

  @Test
  public void readProtocolVersion() throws Exception {
    Protocol protocol = parser.parseJson(jsonString);

    assertThat(protocol).isNotNull();
    assertThat(protocol.getVersion()).isEqualTo(0);
  }

  @Test
  public void readStatusId() throws Exception {
    Protocol protocol = parser.parseJson(jsonString);

    assertThat(protocol.getStatus()).isEqualTo(ProtocolStatus.OK.getId());
  }

  @Test
  public void readStatusMessage() throws Exception {
    Protocol protocol = parser.parseJson(jsonString);

    assertThat(protocol.getStatusMessage()).isEqualTo(ProtocolStatus.OK.getMessage());
  }

  @Test
  public void versionNotYetNegotiated() throws Exception {
    Protocol protocol = parser.parseJson(jsonString);

    assertThat(protocol.getNegotiatedVersion()).isNull();
  }
}
