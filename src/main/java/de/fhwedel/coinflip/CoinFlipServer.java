package de.fhwedel.coinflip;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.net.DefaultSocketFactory;
import org.apache.log4j.Logger;

public class CoinFlipServer {
  private final int port;
  private static final Logger logger = Logger.getLogger(CoinFlip.class);
  private static final String CLOSE_MESSAGE = "ABORT_SERVER" + System.lineSeparator();
  private boolean running = true;

  public CoinFlipServer(int port) {
    this.port = port;
  }

  public void start() {
    DefaultSocketFactory factory = new DefaultSocketFactory();
    try (ServerSocket serverSocket = factory.createServerSocket(port)) {
      new Thread(new KeyboardListener()).start();

      while (running) {
        Socket client = serverSocket.accept();
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

    public ConnectionHandler(Socket clientSocket) {
      this.client = clientSocket;
    }

    @Override
    public void run() {
      try (InputStream inputStream = client.getInputStream()) {
        String message = IOUtils.toString(inputStream);

        if (message.equals(CLOSE_MESSAGE)) {
          logger.info("Received CLOSE_MESSAGE");
          return;
        }

        logger.info("Received from client:");
        logger.info(message);
      } catch (IOException e) {
        logger.error("Error in reading from client-sockets input stream. Aborting..", e);
        throw new RuntimeException(e);
      }
    }
  }


  private class KeyboardListener implements Runnable {
    @Override
    public void run() {
      System.out.println("Enter 'exit' to quit the server.");
      while (running) {
        LineIterator lineIterator = null;
        try {
          lineIterator = IOUtils.lineIterator(System.in, "UTF-8");
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
