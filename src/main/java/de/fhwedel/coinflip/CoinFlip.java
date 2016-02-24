package de.fhwedel.coinflip;

import java.net.InetAddress;
import java.security.Security;
import java.util.Optional;

import javax.swing.*;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import de.fhwedel.coinflip.gui.UserInterface;
import de.fhwedel.coinflip.protocol.model.Sids;
import de.fhwedel.coinflip.protocol.model.Versions;
import de.fhwedel.coinflip.protocol.model.sid.Sid;

public class CoinFlip {

  public static final Versions supportedVersions = Versions.containing("1.0");
  public static Sids supportedSids = Sids.containing(Sid.SRA1024SHA1, Sid.SRA2048SHA1,
      Sid.SRA3072SHA1, Sid.SRA1024SHA256, Sid.SRA2048SHA512, Sid.SRA2048SHA256);

  private static Logger logger = Logger.getLogger(CoinFlip.class);

  public static void main(String[] args) {
    Security.addProvider(new BouncyCastleProvider());

    // re-enable md5 hashes (disabled in newest java8 release)
    // this is dirty, the broker should not use md5-hash signatures in his certificates!
    java.security.Security.setProperty("jdk.tls.disabledAlgorithms", "SSLv3, DH keySize < 768");
    java.security.Security.setProperty("jdk.certpath.disabledAlgorithms",
        "MD2, RSA keySize < 1024");

    if (args.length == 1) {
      if (args[0].equals("--gui")) {
        UserInterface userInterface = new UserInterface();
        userInterface.pack();
        userInterface.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        userInterface.setVisible(true);
        return;
      }
    }

    if (args.length < 2) {
      printUsageAndExit();
    }

    if (args.length > 3) {
      printUsageAndExit();
    }

    switch (args[0]) {
      case "--server":
        if (args.length != 3) {
          printUsageAndExit();
        }
        try {
          int port = Integer.parseInt(args[1]);
          logger.info("Starting as a server.");
          CoinFlipServer coinFlipServer =
              new CoinFlipServer(port, CoinFlipServerMode.INTERACTIVE, args[2]);
          coinFlipServer.start();
        } catch (NumberFormatException e) {
          printUsageAndExit();
        }
        break;
      case "--server-silent":
        try {
          if (args.length != 3) {
            printUsageAndExit();
          }
          int port = Integer.parseInt(args[1]);
          logger.info("Starting as a silent server. Close by killing the process or CTRL+C.");
          CoinFlipServer coinFlipServer =
              new CoinFlipServer(port, CoinFlipServerMode.SILENT, args[2]);
          coinFlipServer.start();
        } catch (NumberFormatException e) {
          printUsageAndExit();
        }
        break;
      case "--client":
        if (args.length != 2) {
          printUsageAndExit();
        }
        logger.info("Starting as a client.");
        String addressString = args[1];
        String[] split = addressString.split(":");

        logger.debug("Parsed server ip: " + split[0]);
        logger.debug("Parsed server port: " + split[1]);

        try {
          String address = String.valueOf(split[0]);
          int port = Integer.parseInt(split[1]);

          InetAddress inetAddress = InetAddress.getByName(address);

          CoinFlipClient coinFlipClient = new CoinFlipClient(Optional.empty(), Optional.empty());
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
.println(
        "Usage: start the program either with --client <ip:port>, --server[-silent] <port> <servername> or --gui");
    System.exit(1);
  }
}
