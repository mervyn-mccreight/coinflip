package de.fhwedel.coinflip;

import org.apache.log4j.Logger;

import de.fhwedel.coinflip.protocol.model.Versions;

public class CoinFlip {

  public static final Versions supportedVersions = Versions.containing("1.0");

  private static Logger logger = Logger.getLogger(CoinFlip.class);

  public static void main(String[] args) {
    if (args.length != 2) {
      printUsageAndExit();
    }

    switch (args[0]) {
      case "--server":
        try {
          int port = Integer.parseInt(args[1]);
          logger.info("Starting as a server.");
          CoinFlipServer coinFlipServer = new CoinFlipServer(port);
          coinFlipServer.start();
        } catch (NumberFormatException e) {
          printUsageAndExit();
        }
        break;
      case "--client":
        logger.info("Starting as a client.");
        CoinFlipClient coinFlipClient = new CoinFlipClient(null);
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
