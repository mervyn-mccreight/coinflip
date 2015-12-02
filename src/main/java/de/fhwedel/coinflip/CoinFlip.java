package de.fhwedel.coinflip;

import org.apache.log4j.Logger;

public class CoinFlip {

  private static Logger logger = Logger.getLogger(CoinFlip.class);

  public static void main(String[] args) {
    if (args.length != 2) {
      printUsageAndExit();
    }

    switch (args[0]) {
      case "--server":
        logger.info("Starting as a server.");
        break;
      case "--client":
        logger.info("Starting as a client.");
        break;
      default:
        printUsageAndExit();
    }
  }

  private static void printUsageAndExit() {
    logger.info("Main called with wrong parameters.");
    System.out
        .println("Usage: start the program either with --client <ip:port> or --server <port>");
    System.exit(1);
  }
}
