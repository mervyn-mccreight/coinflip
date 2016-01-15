package de.fhwedel.coinflip;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Optional;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.google.common.collect.Lists;

import de.fhwedel.coinflip.protocol.ClientProtocolHandler;
import de.fhwedel.coinflip.protocol.ProtocolHandler;
import de.fhwedel.coinflip.protocol.io.ProtocolParser;
import de.fhwedel.coinflip.protocol.model.BaseProtocol;
import de.fhwedel.coinflip.protocol.model.BaseProtocolBuilder;
import de.fhwedel.coinflip.protocol.model.id.ProtocolId;
import de.fhwedel.coinflip.protocol.model.status.ProtocolStatus;
import de.fhwedel.ssl.CreateSSLSocket;

public class CoinFlipClient {

  public static final String[] coin = {"HEAD", "TAIL"};

  public ConnectedClient connect(InetAddress serverAddress, int port)
      throws ConnectionFailedException {
    try {
      return new ConnectedClient(CreateSSLSocket.getSSLSocket(serverAddress.getHostAddress(), port,
          "ssl-data/client", "fhwedel", "ssl-data/root"));
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
    private ProtocolHandler handler;
    private ProtocolParser parser;
    private Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    private ConnectedClient(Socket connection) {
      this.connection = connection;
      this.handler = new ClientProtocolHandler();
      this.parser = new ProtocolParser();
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

        logger.debug("Sending game request to server.");
        IOUtils.write(initialMessage + System.lineSeparator(), out);

        Scanner scanner = new Scanner(in);

        while (statusIsOk) {
          if (!scanner.hasNextLine()) {
            continue;
          }

          String message = scanner.nextLine();
          logger.debug("Received from server: ");
          logger.debug(message);

          Optional<BaseProtocol> answer = handler.work(parser.parseJson(message));

          BaseProtocol protocol = answer.orElseThrow(UnknownProtocolException::new);

          if (protocol.getStatus() != ProtocolStatus.OK.getId()) {
            statusIsOk = false;
          }

          IOUtils.write(parser.toJson(protocol) + System.lineSeparator(), out);
        }

        return new CoinFlipClient();
      } catch (IOException e) {
        throw new RuntimeException(e);
      } catch (UnknownProtocolException e) {
        logger.info("Received unexpected message from the server. Aborting connection.");
        return new CoinFlipClient();
      }
    }
  }
}
