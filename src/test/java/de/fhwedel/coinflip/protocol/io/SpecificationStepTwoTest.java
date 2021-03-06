package de.fhwedel.coinflip.protocol.io;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import de.fhwedel.coinflip.protocol.model.BaseProtocol;
import de.fhwedel.coinflip.protocol.model.Sids;
import de.fhwedel.coinflip.protocol.model.id.ProtocolId;
import de.fhwedel.coinflip.protocol.model.sid.Sid;

public class SpecificationStepTwoTest extends AbstractProtocolSpecificationTest {
  private String jsonString;
  private ProtocolParser parser;

  @Before
  public void setUp() throws Exception {
    // given
    File file = new File("src/test/resources/protocol/specification/2.json");
    jsonString = FileUtils.readFileToString(file);
    parser = new ProtocolParser();
  }

  @Test
  public void getStep() throws Exception {
    BaseProtocol baseProtocol = parser.parseJson(jsonString).get();
    assertProtocolStep(baseProtocol, ProtocolId.TWO);
  }

  @Test
  public void pAndQAreEmpty() throws Exception {
    BaseProtocol protocol = parser.parseJson(jsonString).get();
    assertEmptyPandQ(protocol);
  }

  @Test
  public void getSid() throws Exception {
    BaseProtocol protocol = parser.parseJson(jsonString).get();
    assertSid(protocol, Sid.SRA1024SHA1);
  }

  @Test
  public void availableSids() throws Exception {
    BaseProtocol protocol = parser.parseJson(jsonString).get();

    List<Sids> availableSids = protocol.getAvailableSidsIds();
    assertThat(availableSids).hasSize(1);

    Sids expected = Sids.containing(Sid.SRA1024SHA1, Sid.SRA2048SHA1, Sid.SRA3072SHA1);
    Sids sids = availableSids.get(0);
    assertThat(sids).isEqualTo(expected);
  }

  @Test
  public void payloadIsEmpty() throws Exception {
    BaseProtocol protocol = parser.parseJson(jsonString).get();
    assertEmptyPayload(protocol);
  }
}
