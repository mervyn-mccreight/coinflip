package de.fhwedel.ssl.test;

import org.junit.Test;

public class TLSTest {
	
	public static String data = "src/test/resources/data/";
	public static String host = "localhost";
	public static int port = 4444;
	public static String password = "fhwedel";
	
	@Test
	public void CertTest() throws Exception {
		
		String store;
//		store = data + "default";
//		store = data + "defaultEC";
		store = data + "defaultCA";
		
//		store = data + "FalsePositivRootExpired"; // expires 24.12.2015
//		store = data + "FalsePositivRootIsNotCA";

//		store = data + "ErrorInterIsNotCA";
		
//		store = data + "ErrorClientsKeyUsage";
		
//		store = data + "SelfSignedClients";
		



		String truststore = store + "/root";
		String client = store + "/client";
		String server = store + "/server";

		new Thread(new Server(port, server, password, truststore)).start();
		new Client(port, host, client, password, truststore).run();
		
		
	}
}
