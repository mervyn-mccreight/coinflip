package de.fhwedel.coinflip.protocol.io;

import java.io.File;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import de.fhwedel.coinflip.protocol.model.BaseProtocol;

public class StepTwoUnknownSid extends AbstractProtocolSpecificationTest {
  private String jsonString;
  private ProtocolParser parser;

  @Before
  public void setUp() throws Exception {
    // given
    File file = new File("src/test/resources/protocol/specification/2_unknown_sid.json");
    jsonString = FileUtils.readFileToString(file);
    parser = new ProtocolParser();
  }

  @Test
  public void unknownSid_isParsable() throws Exception {
    Optional<BaseProtocol> protocol = parser.parseJson(jsonString);
  }
}
