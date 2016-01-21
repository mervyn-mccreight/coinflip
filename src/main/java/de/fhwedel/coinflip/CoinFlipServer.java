package de.fhwedel.coinflip;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.log4j.Logger;

import com.google.common.collect.Maps;

import de.fhwedel.coinflip.protocol.ProtocolHandler;
import de.fhwedel.coinflip.protocol.ServerProtocolHandler;
import de.fhwedel.coinflip.protocol.io.ProtocolParser;
import de.fhwedel.coinflip.protocol.model.BaseProtocol;
import de.fhwedel.coinflip.protocol.model.id.ProtocolId;
import de.fhwedel.ssl.CreateSSLServerSocket;
import gr.planetz.PingingService;
import gr.planetz.impl.HttpPingingService;

public class CoinFlipServer {
  private final int port;
  private static final Logger logger = Logger.getLogger(CoinFlip.class);
  private static final String CLOSE_MESSAGE = "ABORT_SERVER";
  private boolean running = true;
  private static final IdGenerator idGenerator = new IdGenerator();
  public static final Map<Integer, KeyPair> keyMap = Maps.newHashMap();
  private final CoinFlipServerMode mode;
  private static final String BROKER_URI = "https://52.35.76.130:8443/broker/1.0/join";
  private Optional<PingingService> pingingService = Optional.empty();
  private String nickname;

  public CoinFlipServer(int port, CoinFlipServerMode mode, String nickname) {
    this.port = port;
    this.mode = mode;
    this.nickname = nickname;
  }

  public void start() {
    try (ServerSocket serverSocket = CreateSSLServerSocket.GetSSLServerSocket(port,
        "ssl-data/server", "fhwedel", "ssl-data/root")) {
      if (CoinFlipServerMode.INTERACTIVE.equals(this.mode)) {
        new Thread(new KeyboardListener()).start();
      }

      String hostAddress = InetAddress.getLocalHost().getHostAddress();
      logger.debug("Started server at: " + hostAddress);

      String myUrl = hostAddress + ":" + String.valueOf(this.port);

      pingingService = Optional.of(new HttpPingingService(BROKER_URI, nickname, myUrl,
          "ssl-data/memc_keystore.jks", "secret"));

      pingingService.ifPresent(PingingService::start);

      while (running) {
        Socket client = serverSocket.accept();
        logger.debug(
            "Accepting connection to server socket from: " + client.getInetAddress().toString());
        new Thread(new ConnectionHandler(client)).start();
      }
    } catch (Exception e) {
      logger.error("Error in creating/listening to server socket. Aborting ...", e);
      throw new RuntimeException(e);
    } finally {
      pingingService.ifPresent(PingingService::stop);
      logger.debug("Server socket shutting down.");
    }
  }

  private static class IdGenerator {
    private int id = 0;

    public int getNext() {
      return this.id++;
    }
  }

  private class ConnectionHandler implements Runnable {
    private final Logger logger = Logger.getLogger(ConnectionHandler.class);
    private Socket client;
    private final ProtocolParser parser = new ProtocolParser();
    private final int sessionId;
    private final ProtocolHandler handler;

    public ConnectionHandler(Socket clientSocket) {
      this.client = clientSocket;
      this.sessionId = idGenerator.getNext();
      this.handler = new ServerProtocolHandler(sessionId);
    }

    @Override
    public void run() {

      try (InputStream inputStream = client.getInputStream()) {
        while (!client.isClosed() && running) {
          Scanner scanner = new Scanner(inputStream);

          // todo (10.12.2015): this breaks the server exit, if this is waiting forever.
          if (!scanner.hasNextLine()) {
            continue;
          }

          String message = scanner.nextLine();

          logger.info("Received from client:");
          logger.info(message);

          if (message.equals(CLOSE_MESSAGE)) {
            logger.info("Received CLOSE_MESSAGE");
            return;
          }

          Optional<BaseProtocol> response = handler.work(parser.parseJson(message));
          String answerString = parser.toJson(
response.orElseThrow(UnknownProtocolException::new));

          logger.debug("Sending answer to client:");
          logger.debug(answerString);

          IOUtils.write(answerString + System.lineSeparator(), client.getOutputStream());

          if (response.isPresent()) {
            if (response.get().getId().equals(ProtocolId.SEVEN)) {
              logger.debug("Sent last protocol step. Closing connection.");
              client.close();
            }
          }
        }
      } catch (IOException e) {
        logger.error("Error in reading from client-sockets input stream. Aborting..", e);
        throw new RuntimeException(e);
      } catch (UnknownProtocolException e) {
        logger.info("Retrieved unknown protocol.");
      }
      logger.info("Closing connection to: " + client.getInetAddress().toString());
      unregisterKeyPair();
    }

    private void unregisterKeyPair() {
      logger.debug("Unregistering KeyPair for sessionId " + sessionId);
      keyMap.remove(sessionId);
    }
  }

  private class KeyboardListener implements Runnable {
    @Override
    public void run() {
      System.out.println("Enter 'exit' to quit the server.");
      while (running) {
        LineIterator lineIterator = null;
        try {
          // todo (04.12.2015): which charset are we encoding the string for streaming?
          lineIterator = IOUtils.lineIterator(System.in, Charset.defaultCharset());
          System.out.print("> ");
          while (lineIterator.hasNext()) {
            System.out.print("> ");
            String input = lineIterator.nextLine();
            if (input.equals("exit")) {
              System.out.println("Exiting Server ...");
              running = false;
              Socket socket = new Socket(InetAddress.getLocalHost(), port);
              socket.getOutputStream().write(CLOSE_MESSAGE.getBytes());
              socket.getOutputStream().flush();
              socket.close();
              break;
            }
          }
        } catch (IOException e) {
          logger.error("Error reading from System.in. Aborting ...");
          throw new RuntimeException(e);
        } finally {
          logger.debug("Closing Keyboard Listener ...");
          LineIterator.closeQuietly(lineIterator);
        }
      }
    }
  }
}
