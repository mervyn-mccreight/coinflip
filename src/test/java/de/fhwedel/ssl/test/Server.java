package de.fhwedel.ssl.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;

import de.fhwedel.ssl.CreateSSLServerSocket;

public class Server implements Runnable {
	int port;
	String trustStore;
	String keyStore;
	String keyStorePassword;

	public Server(int port, String keyStore, String keyStorePassword, String trustStore) throws Exception {
		this.port = port;
		this.trustStore = trustStore;
		this.keyStore = keyStore;
		this.keyStorePassword = keyStorePassword;
	}

	@Override
	public void run() {
		try {
			SSLServerSocket sslServerSocket = CreateSSLServerSocket.GetSSLServerSocket(port, keyStore, keyStorePassword,
					trustStore);

			System.out.println("Server: Server started");

			SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();

			System.out.println("Server: " + sslSocket.getSession().getCipherSuite());

			BufferedReader in = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
			PrintWriter out = new PrintWriter(sslSocket.getOutputStream(), true);

			System.out.println(in.readLine());

			out.println("Server: Hello Client\n");
			out.flush();

			in.close();
			out.close();
			sslSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
