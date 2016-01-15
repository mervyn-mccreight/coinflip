package de.fhwedel.ssl.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.net.ssl.SSLSocket;

import org.junit.Assert;

import de.fhwedel.ssl.CreateSSLSocket;

public class Client implements Runnable {
	private int port;
	private String host;
	private String keyStore;
	private String keyStorePassword;
	private String trustStore;

	public Client(int port, String host, String keyStore, String keyStorePassword, String trustStore) {
		this.port = port;
		this.host = host;
		this.keyStore = keyStore;
		this.keyStorePassword = keyStorePassword;
		this.trustStore = trustStore;
	}

	@Override
	public void run() {

		try {
			SSLSocket socket = CreateSSLSocket.getSSLSocket(host, port, keyStore, keyStorePassword, trustStore);

			System.out.println("Client: " + socket.getSession().getCipherSuite());

			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

			out.println("Client: Hallo Server");
			out.flush();
			System.out.println(in.readLine());

			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
