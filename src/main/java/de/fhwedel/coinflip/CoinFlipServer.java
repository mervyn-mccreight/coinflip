package de.fhwedel.coinflip;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.Scanner;

import de.fhwedel.coinflip.protocol.ProtocolHandler;
import de.fhwedel.coinflip.protocol.io.ProtocolParser;
import de.fhwedel.coinflip.protocol.model.BaseProtocol;
import de.fhwedel.coinflip.protocol.model.id.ProtocolId;
import de.fhwedel.coinflip.protocol.model.status.ProtocolStatus;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.net.DefaultSocketFactory;
import org.apache.log4j.Logger;

public class CoinFlipServer {
  private final int port;
  private static final Logger logger = Logger.getLogger(CoinFlip.class);
  private static final String CLOSE_MESSAGE = "ABORT_SERVER";
  private boolean running = true;

  public CoinFlipServer(int port) {
    this.port = port;
  }

  public void start() {
    DefaultSocketFactory factory = new DefaultSocketFactory();
    try (ServerSocket serverSocket = factory.createServerSocket(port)) {
      new Thread(new KeyboardListener()).start();

      logger.debug("Started server at: " + serverSocket.toString());

      while (running) {
        Socket client = serverSocket.accept();
        logger.debug(
            "Accepting connection to server socket from: " + client.getInetAddress().toString());
        new Thread(new ConnectionHandler(client)).start();
      }
    } catch (IOException e) {
      logger.error("Error in creating/listening to server socket. Aborting ...", e);
      throw new RuntimeException(e);
    } finally {
      logger.debug("Server socket shutting down.");
    }
  }

  private class ConnectionHandler implements Runnable {
    private final Logger logger = Logger.getLogger(ConnectionHandler.class);
    private Socket client;
    private final ProtocolParser parser = new ProtocolParser();

    public ConnectionHandler(Socket clientSocket) {
      this.client = clientSocket;
    }

    @Override
    public void run() {
      while (!client.isClosed() && running) {
        try (InputStream inputStream = client.getInputStream()) {
          Scanner scanner = new Scanner(inputStream);
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

          String answerString =
 parser.toJson(ProtocolHandler.work(parser.parseJson(message))
              .orElseGet(() -> new BaseProtocol(ProtocolId.ERROR, ProtocolStatus.ERROR,
                  ProtocolStatus.ERROR.getMessage(), null, null)));

          IOUtils.write(answerString + System.lineSeparator(), client.getOutputStream());
        } catch (IOException e) {
          logger.error("Error in reading from client-sockets input stream. Aborting..", e);
          throw new RuntimeException(e);
        }
      }
      logger.info("Closing connection to: " + client.getInetAddress().toString());
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
