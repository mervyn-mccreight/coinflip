package de.fhwedel.coinflip.protocol;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.math.BigInteger;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import de.fhwedel.coinflip.protocol.io.ProtocolParser;
import de.fhwedel.coinflip.protocol.model.BaseProtocol;
import de.fhwedel.coinflip.protocol.model.id.ProtocolId;

public class SpecificationStepSevenTest extends AbstractProtocolSpecificationTest {
  private BaseProtocol protocol;

  private static final BigInteger a1 = new BigInteger(
      "17665904071322043195728095639140898894350784096070105386234255987578098056941368327990346460272880079406616416037627305060888338175944695883610156862061850647577135591138418024461256454750099955904240571185758506999871765166172086375032743230219380680632284549175333629624588870175933847446584430146564121139611038527644621276750462421751079898541858358393854256644489288109751999373291205348895711862118501479343045308926340943231442327830046072200329234672968572908813269969768360131847044988551487368594859481933518615278223812364664869369002993194784370868510830934292528723007096748380021717457031992331206249589");
  private static final BigInteger a2 = new BigInteger(
      "5458282484434444286586045114782639553167603656664569949341288085097698885283459256124086965664999321906066762331854862754563659536393761717089751866075233700414178231468369318753675172070038659851180661392663860373216006421505298405133804455607344271320764815863268881713345777283957662959597926489330743459517435825197698022775223284984993780012432321281008694002641056864605874459687028402998341253854654640872062396553944683743793140046064376513097384289288209882437334264922229651343888847035413927805889998634773863064867290469360384545404794951310809349655477424888324396711298528741687799159279927474430385123");

  private static final BigInteger b1 = new BigInteger(
      "16780973002733397557355000281233283458278070512151134116314636623778813690858216886319252865915516798162578764541189807241088485540798617035984822029383183927834405277093101467405303668454902615738364828757090185211170086003163145325093167043422691393235202259329911053126217398238054780689724736659117165653082816059007996108729364978251829136124740828692612642476216461083616641516933992653214433655129131723234309476456990254460461555487346102648158514956544414610117725481252832729299851328919217371621587161736802436610210945640301869644048780440605781262701725410214058253077341581629763857675177320711393544923");
  private static final BigInteger b2 = new BigInteger(
      "3785396059669785951008590006717360111722223952320207565412103965929874766860026632315408018668501381216463019481473308222058612624817729851301417969071693365595506370739841818896758538270930624979706379635316542468937750155243066886407832465576736394002290597311833384891941030364405329425970825707685217933621342260765742226658759085479443127120019192379355645350582759211107916934677459433314574210933108837425024868096253310486705589551103439934454030974454753332939947212046853545189393699880792297852939124212287764488367596122452971185903116981602954102439643566301137594487643527643794024189645349941192055333");

  @Before
  public void setUp() throws Exception {
    // given
    File file = new File("src/test/resources/protocol/specification/7.json");
    String jsonString = FileUtils.readFileToString(file);
    ProtocolParser parser = new ProtocolParser();
    protocol = parser.parseJson(jsonString);
  }

  @Test
  public void step() throws Exception {
    assertProtocolStep(protocol, ProtocolId.SEVEN);
  }

  @Test
  public void bothDecryptionKeysAreRevealed() throws Exception {
    assertThat(protocol.getPrivateParametersForKeyA()).containsExactly(a1, a2);
    assertThat(protocol.getPrivateParametersForKeyB()).containsExactly(b1, b2);
  }
}