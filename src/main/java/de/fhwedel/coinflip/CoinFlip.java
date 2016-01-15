package de.fhwedel.coinflip;

import java.net.InetAddress;
import java.security.Security;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import de.fhwedel.coinflip.protocol.model.Sids;
import de.fhwedel.coinflip.protocol.model.Versions;
import de.fhwedel.coinflip.protocol.model.sid.Sid;

public class CoinFlip {

  public static final Versions supportedVersions = Versions.containing("1.0");
  public static Sids supportedSids = Sids.containing(Sid.SRA1024SHA1, Sid.SRA2048SHA1,
      Sid.SRA3072SHA1, Sid.SRA1024SHA256, Sid.SRA2048SHA512);

  private static Logger logger = Logger.getLogger(CoinFlip.class);

  public static void main(String[] args) {
    Security.addProvider(new BouncyCastleProvider());

    if (args.length != 2) {
      printUsageAndExit();
    }

    switch (args[0]) {
      case "--server":
        try {
          int port = Integer.parseInt(args[1]);
          logger.info("Starting as a server.");
          CoinFlipServer coinFlipServer = new CoinFlipServer(port, CoinFlipServerMode.INTERACTIVE);
          coinFlipServer.start();
        } catch (NumberFormatException e) {
          printUsageAndExit();
        }
        break;
      case "--server-silent":
        try {
          int port = Integer.parseInt(args[1]);
          logger.info("Starting as a silent server. Close by killing the process or CTRL+C.");
          CoinFlipServer coinFlipServer = new CoinFlipServer(port, CoinFlipServerMode.SILENT);
          coinFlipServer.start();
        } catch (NumberFormatException e) {
          printUsageAndExit();
        }
        break;
      case "--client":
        logger.info("Starting as a client.");
        String addressString = args[1];
        String[] split = addressString.split(":");

        logger.debug("Parsed server ip: " + split[0]);
        logger.debug("Parsed server port: " + split[1]);

        try {
          String address = String.valueOf(split[0]);
          int port = Integer.parseInt(split[1]);

          InetAddress inetAddress = InetAddress.getByName(address);

          CoinFlipClient coinFlipClient = new CoinFlipClient();
          CoinFlipClient.ConnectedClient connectedClient =
              coinFlipClient.connect(inetAddress, port);
          connectedClient.play();

        } catch (CoinFlipClient.ConnectionFailedException e) {
          logger.info("Connection to server failed. Aborting...");
        } catch (Exception e) {
          printUsageAndExit();
        }
        break;
      default:
        printUsageAndExit();
    }
    logger.info("CoinFlip Client shut down.");
  }

  private static void printUsageAndExit() {
    logger.info("Main called with wrong parameters.");
    System.out
        .println("Usage: start the program either with --client <ip:port> or --server <port>");
    System.exit(1);
  }
}
