package de.fhwedel.coinflip.protocol.io;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import de.fhwedel.coinflip.protocol.model.BaseProtocol;
import de.fhwedel.coinflip.protocol.model.Versions;
import de.fhwedel.coinflip.protocol.model.id.ProtocolId;
import de.fhwedel.coinflip.protocol.model.status.ProtocolStatus;

public class SpecificationStepZeroTest extends AbstractProtocolSpecificationTest {
  private String jsonString;
  private ProtocolParser parser;

  @Before
  public void setUp() throws Exception {
    // given
    File file = new File("src/test/resources/protocol/specification/0.json");
    jsonString = FileUtils.readFileToString(file);
    parser = new ProtocolParser();
  }

  @Test
  public void readProtocolVersion() throws Exception {
    BaseProtocol protocol = parser.parseJson(jsonString).get();
    assertProtocolStep(protocol, ProtocolId.ZERO);
  }

  @Test
  public void readStatusId() throws Exception {
    BaseProtocol protocol = parser.parseJson(jsonString).get();
    assertStatusId(protocol, ProtocolStatus.OK);
  }

  @Test
  public void readStatusMessage() throws Exception {
    BaseProtocol protocol = parser.parseJson(jsonString).get();
    assertThat(protocol.getStatusMessage()).isEqualTo(ProtocolStatus.OK.getMessage());
  }

  @Test
  public void versionNotYetNegotiated() throws Exception {
    BaseProtocol protocol = parser.parseJson(jsonString).get();
    assertNoNegotiatedVersion(protocol);
  }

  @Test
  public void ownProposedVersionIsJustOne() throws Exception {
    BaseProtocol protocol = parser.parseJson(jsonString).get();

    List<Versions> proposedVersions = protocol.getProposedVersions();
    assertThat(proposedVersions).hasSize(1);

    Versions versions = proposedVersions.get(0);
    assertThat(versions.count()).isEqualTo(1);

    Set<String> versionSet = versions.get();
    assertThat(versionSet).hasSize(1);

    assertThat(versionSet).containsExactly("1.0");
  }
}
