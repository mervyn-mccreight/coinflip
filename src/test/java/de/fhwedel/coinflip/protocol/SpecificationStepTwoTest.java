package de.fhwedel.coinflip.protocol;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import de.fhwedel.coinflip.protocol.io.ProtocolParser;
import de.fhwedel.coinflip.protocol.model.BaseProtocol;
import de.fhwedel.coinflip.protocol.model.Sids;
import de.fhwedel.coinflip.protocol.model.id.ProtocolId;

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
    assertSid(protocol, 0);
  }

  @Test
  public void availableSids() throws Exception {
    BaseProtocol protocol = parser.parseJson(jsonString).get();

    List<Sids> availableSids = protocol.getAvailableSids();
    assertThat(availableSids).hasSize(1);

    Sids expected = new Sids(Lists.newArrayList(0, 1, 2));
    Sids sids = availableSids.get(0);
    assertThat(sids).isEqualTo(expected);
  }

  @Test
  public void payloadIsEmpty() throws Exception {
    BaseProtocol protocol = parser.parseJson(jsonString).get();
    assertEmptyPayload(protocol);
  }
}
