package de.fhwedel.coinflip;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Optional;
import java.util.Scanner;

import javax.swing.*;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.google.common.collect.Lists;

import de.fhwedel.coinflip.cipher.exception.CipherException;
import de.fhwedel.coinflip.protocol.ClientProtocolHandler;
import de.fhwedel.coinflip.protocol.io.ProtocolParser;
import de.fhwedel.coinflip.protocol.model.BaseProtocol;
import de.fhwedel.coinflip.protocol.model.BaseProtocolBuilder;
import de.fhwedel.coinflip.protocol.model.id.ProtocolId;
import de.fhwedel.coinflip.protocol.model.status.ProtocolStatus;
import de.fhwedel.ssl.CreateSSLSocket;

public class CoinFlipClient {

  public static final String[] coin = {"HEAD", "TAIL"};

  private Optional<JLabel> progressLabel;
  private Optional<JProgressBar> progressBar;

  public CoinFlipClient(Optional<JLabel> progressLabel, Optional<JProgressBar> progressBar) {
    this.progressLabel = progressLabel;
    this.progressBar = progressBar;
  }

  public ConnectedClient connect(InetAddress serverAddress, int port)
      throws ConnectionFailedException {
    try {
      this.progressLabel.ifPresent(label -> label.setText("Connecting..."));
      return new ConnectedClient(CreateSSLSocket.getSSLSocket(serverAddress.getHostAddress(), port,
          "ssl-data/client", "fhwedel", "ssl-data/root"), this.progressLabel, this.progressBar);
    } catch (Exception e) {
      throw new ConnectionFailedException(e);
    }
  }

  public class ConnectionFailedException extends RuntimeException {
    public ConnectionFailedException(Throwable cause) {
      super(cause);
    }
  }


  public class ConnectedClient {
    private Socket connection;
    private ClientProtocolHandler handler;
    private ProtocolParser parser;
    private Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    private Optional<JLabel> progressLabel;
    private Optional<JProgressBar> progressBar;

    private ConnectedClient(Socket connection, Optional<JLabel> progressLabel,
        Optional<JProgressBar> progressBar) {
      this.connection = connection;
      this.parser = new ProtocolParser();
      this.progressLabel = progressLabel;
      this.progressBar = progressBar;

      this.handler = new ClientProtocolHandler(this.progressLabel, this.progressBar);

      this.progressBar.ifPresent(bar -> bar.setValue(1));
      this.progressLabel.ifPresent(label -> label.setText("Established Connection."));
    }

    public CoinFlipClient play() {
      try (InputStream in = connection.getInputStream();
          OutputStream out = connection.getOutputStream()) {

        boolean statusIsOk = true;

        BaseProtocol initialProtocol =
            new BaseProtocolBuilder().setId(ProtocolId.ZERO).setStatus(ProtocolStatus.OK)
                .setProposedVersions(Lists.newArrayList(CoinFlip.supportedVersions))
                .createBaseProtocol();

        String initialMessage = parser.toJson(initialProtocol);

        logger.debug("Sending message:");
        logger.debug(initialMessage);

        this.progressLabel.ifPresent(label -> label.setText("Sending initial message to server."));
        this.progressBar.ifPresent(bar -> bar.setValue(2));

        IOUtils.write(initialMessage + System.lineSeparator(), out);

        Scanner scanner = new Scanner(in);

        while (statusIsOk) {
          if (!scanner.hasNextLine()) {
            continue;
          }

          String message = scanner.nextLine();
          logger.debug("Received from server: ");
          logger.debug(message);

          Optional<BaseProtocol> messageProtocol = parser.parseJson(message);

          if (messageProtocol.isPresent()) {
            if (messageProtocol.get().getId().equals(ProtocolId.SEVEN)) {
              logger.debug("Received last protocol step. Game has ended.");
              try {
                String result = handler.determineCoinResult(messageProtocol.get());
                logger.info("Coinflip result is: " + result);
                logger.info("We guessed: " + messageProtocol.get().getDesiredCoinSide());

                this.progressBar.ifPresent(bar -> bar.setValue(bar.getMaximum()));

                if (result.equals(messageProtocol.get().getDesiredCoinSide())) {
                  this.progressLabel
                      .ifPresent(label -> label.setText("The game is over. You won."));
                } else {
                  this.progressLabel
                      .ifPresent(label -> label.setText("The game is over. You lost."));
                }

              } catch (CipherException e) {
                logger.error("Error decoding the result");
                this.progressLabel.ifPresent(label -> label.setText("Error in decoding result."));
                e.printStackTrace();
              }
              break;
            }
          }

          Optional<BaseProtocol> answer = handler.work(messageProtocol);
          BaseProtocol protocol = answer.orElseThrow(UnknownProtocolException::new);

          if (protocol.getStatus() != ProtocolStatus.OK.getId()) {
            statusIsOk = false;
          }

          String response = parser.toJson(protocol);
          logger.debug("Sending response:");
          logger.debug(response);
          IOUtils.write(response + System.lineSeparator(), out);
        }

        return new CoinFlipClient(this.progressLabel, this.progressBar);
      } catch (IOException e) {
        throw new RuntimeException(e);
      } catch (UnknownProtocolException e) {
        logger.info("Received unexpected message from the server. Aborting connection.");
        return new CoinFlipClient(this.progressLabel, this.progressBar);
      }
    }
  }
}
