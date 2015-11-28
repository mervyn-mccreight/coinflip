package de.fhwedel.coinflip.protocol;

import java.io.File;
import java.math.BigInteger;
import java.util.List;

import com.google.common.collect.Lists;
import de.fhwedel.coinflip.protocol.model.Sids;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import de.fhwedel.coinflip.protocol.io.ProtocolParser;
import de.fhwedel.coinflip.protocol.model.BaseProtocol;
import de.fhwedel.coinflip.protocol.model.id.ProtocolId;

import static org.assertj.core.api.Assertions.assertThat;

public class SpecificationStepThreeTest extends AbstractProtocolSpecificationTest {
  private BaseProtocol protocol;

  private static final BigInteger P = new BigInteger(
      "169379360279398951470915431776185802635886031570851710575832256411028502730013620860721045099997545330921754798657858905471926447219600556415084811060987782653948827302054473717905424578183026891250606795101044267874755082294403091699694797827261430715936195570907595156899579933813400085109162617844113844547");
  private static final BigInteger Q = new BigInteger(
      "114924398764812957944831032002087408953206628445498635024666292906788674761238545054367886948408512044909921622180950744578690626599399625744825077691933093974693821853355473354927510305455832726865437945398180311899568603050727234787444780587700937747922135586151652835372588372420972920925150424378548904027");

  @Before
  public void setUp() throws Exception {
    // given
    File file = new File("src/test/resources/protocol/specification/3.json");
    String jsonString = FileUtils.readFileToString(file);
    ProtocolParser parser = new ProtocolParser();
    protocol = parser.parseJson(jsonString);
  }

  @Test
  public void step() throws Exception {
    assertProtocolStep(protocol, ProtocolId.THREE);
  }

  @Test
  public void emptyPayload() throws Exception {
    assertEmptyPayload(protocol);
  }

  @Test
  public void assertPandQ() throws Exception {
    assertPandQ(protocol, P, Q);
  }

  @Test
  public void availableSids() throws Exception {
    List<Sids> availableSids = protocol.getAvailableSids();
    assertThat(availableSids).hasSize(2);

    Sids expected = new Sids(Lists.newArrayList(0, 1, 2));
    Sids sids1 = availableSids.get(0);
    Sids sids2 = availableSids.get(1);
    assertThat(sids1).isEqualTo(expected);
    assertThat(sids2).isEqualTo(expected);
  }
}
