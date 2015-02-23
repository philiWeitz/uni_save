package org.uta.tcp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

public class ServerMain {
	private static Logger LOG = LogManager.getLogger(ServerMain.class);

	public static void main(String[] args) throws IOException {

		String clientData = StringUtils.EMPTY;
		ServerSocket server = null;

		try {
			server = new ServerSocket(ServerUtil.TCP_PORT);
			System.out.println("Staring up server on port " + ServerUtil.TCP_PORT);

			while (true) {
				System.out.println("Waiting for client data...");
				Socket connectionSocket;

				try {
					connectionSocket = server.accept();
					long received = System.nanoTime();
										
					BufferedReader inFromClient = new BufferedReader(
							new InputStreamReader(connectionSocket.getInputStream()));

					clientData = inFromClient.readLine();
					TCPObject data = new Gson().fromJson(clientData, TCPObject.class);				
						
					double timeDifference = (received - data.getTime()) / 1000;
					LOG.info("Received tag: " + data.getTag() + " (" + timeDifference + "us)");

				} catch (IOException e) {
					LOG.warn("Exception while server.accept method", e);
				}
			}

		} catch (IOException e) {
			LOG.error("Error starting TCP Server on port " + ServerUtil.TCP_PORT, e);

		} finally {
			if (server != null) {
				server.close();
			}
		}
	}
}
