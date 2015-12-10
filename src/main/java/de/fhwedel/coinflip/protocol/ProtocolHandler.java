package de.fhwedel.coinflip.protocol;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.fhwedel.coinflip.CoinFlip;
import de.fhwedel.coinflip.CoinFlipServer;
import de.fhwedel.coinflip.protocol.model.BaseProtocol;
import de.fhwedel.coinflip.protocol.model.BaseProtocolBuilder;
import de.fhwedel.coinflip.protocol.model.Sids;
import de.fhwedel.coinflip.protocol.model.Versions;
import de.fhwedel.coinflip.protocol.model.id.ProtocolId;
import de.fhwedel.coinflip.protocol.model.sid.Sid;
import de.fhwedel.coinflip.protocol.model.status.ProtocolStatus;
import org.bouncycastle.jcajce.provider.asymmetric.sra.SRADecryptionKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class ProtocolHandler {

  private int sessionId;

  public ProtocolHandler(int sessionId) {
    this.sessionId = sessionId;
  }

  public Optional<BaseProtocol> work(Optional<BaseProtocol> protocol) {
    if (protocol.isPresent()) {
      BaseProtocol baseProtocol = protocol.get();
      switch (baseProtocol.getId()) {
        case ZERO:
          return Optional.ofNullable(handleProtocolStepZero(baseProtocol));
        case ONE:
          break;
        case TWO:
          return Optional.ofNullable(handleProtocolStepTwo(baseProtocol));
        case THREE:
          break;
        case FOUR:
          break;
        case FIVE:
          break;
        case SIX:
          break;
        case SEVEN:
          break;
        case ERROR:
          break;
      }
    }

    return Optional.empty();
  }

  private BaseProtocol handleProtocolStepTwo(BaseProtocol given) {
    List<Sids> proposed = given.getAvailableSids();

    if (proposed.isEmpty() || proposed == null) {
      return new BaseProtocolBuilder().setId(ProtocolId.TWO).setStatus(ProtocolStatus.ERROR)
          .setStatusMessage(ProtocolStatus.ERROR.getMessage()).createBaseProtocol();
    }

    List<Sids> newSids = Lists.newArrayList(proposed);
    newSids.add(CoinFlip.supportedSids);

    Set<Sid> intersection = Sets.intersection(newSids.get(0).get(), newSids.get(1).get());

    if (intersection.isEmpty()) {
      return new BaseProtocolBuilder().setId(ProtocolId.TWO).setStatus(ProtocolStatus.ERROR)
          .setStatusMessage(ProtocolStatus.ERROR.getMessage())
          .setAvailableSids(given.getAvailableSids()).createBaseProtocol();
    }

    BaseProtocolBuilder builder = new BaseProtocolBuilder();

    builder.setChosenVersion(given.getNegotiatedVersion());
    builder.setProposedVersions(given.getProposedVersions());
    builder.setStatusMessage(ProtocolStatus.OK.getMessage());
    builder.setStatus(ProtocolStatus.OK);
    builder.setId(ProtocolId.THREE);

    builder.setAvailableSids(newSids);
    Sid chosenSid = intersection.iterator().next();
    builder.setChosenSid(chosenSid);

    // first get the sra key pair generator instance.
    KeyPairGenerator generator;
    try {
      generator = KeyPairGenerator.getInstance("SRA", BouncyCastleProvider.PROVIDER_NAME);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    // provide a bit-size for the key (1024-bit key in this example).
    generator.initialize(chosenSid.getModulus());

    // generate the key pair.
    KeyPair keyPair = generator.generateKeyPair();
    CoinFlipServer.keyMap.put(sessionId, keyPair);

    // get a key factory instance for SRA
    KeyFactory factory;
    try {
      factory = KeyFactory.getInstance("SRA", BouncyCastleProvider.PROVIDER_NAME);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    // extract p and q. you have to use the private key for this, since only the private key
    // contains the information.
    // the key factory fetches the hidden information out of the private key and fills a
    // SRADecryptionKeySpec to provide the information.
    SRADecryptionKeySpec spec;
    try {
      spec = factory.getKeySpec(keyPair.getPrivate(), SRADecryptionKeySpec.class);
    } catch (InvalidKeySpecException e) {
      throw new RuntimeException(e);
    }

    builder.setPublicKeyParts(spec.getP(), spec.getQ());

    return builder.createBaseProtocol();
  }

  private BaseProtocol handleProtocolStepZero(BaseProtocol given) {
    List<Versions> proposed = given.getProposedVersions();

    if (proposed.isEmpty() || proposed == null) {
      return new BaseProtocolBuilder().setId(ProtocolId.ZERO).setStatus(ProtocolStatus.NO_VERSION)
          .setStatusMessage(ProtocolStatus.NO_VERSION.getMessage()).createBaseProtocol();
    }

    List<Versions> newVersions = Lists.newArrayList(proposed);
    newVersions.add(CoinFlip.supportedVersions);

    Set<String> intersection =
        Sets.intersection(newVersions.get(0).get(), newVersions.get(1).get());

    if (intersection.isEmpty()) {
      return new BaseProtocolBuilder().setId(ProtocolId.ZERO).setStatus(ProtocolStatus.NO_VERSION)
          .setStatusMessage(ProtocolStatus.NO_VERSION.getMessage())
          .setProposedVersions(given.getProposedVersions()).createBaseProtocol();
    }

    BaseProtocolBuilder builder = new BaseProtocolBuilder();

    builder.setId(ProtocolId.ONE);
    builder.setStatusMessage(ProtocolStatus.OK.getMessage());
    builder.setStatus(ProtocolStatus.OK);
    builder.setProposedVersions(newVersions);
    builder.setChosenVersion(intersection.iterator().next());
    return builder.createBaseProtocol();
  }
}
