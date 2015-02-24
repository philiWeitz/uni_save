package org.uta.tcp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerMain {
	private static Logger LOG = LogManager.getLogger(ServerMain.class);
	private static List<Socket> clients = new ArrayList<Socket>();

	
	public static void main(String args[]) throws IOException {
		
		ServerSocket server = null;
		
		try {
			server = new ServerSocket(ServerUtil.TCP_PORT);
			
			while (true) {
				System.out.println("Waiting for clients...");
				
				Socket client = server.accept();
				clients.add(client);
				
				new Thread(new ServerThread(client)).start();
			}
			
		} catch (Exception e) {
			LOG.error(e);
			
		} finally {
			if(null != server) {
				server.close();
			}
		}
	}
	
	
	static class ServerThread implements Runnable {
		private Socket client;

		public ServerThread(Socket c) {
			this.client = c;
		}

		public void run() {
			
			System.out.println("Connected to client : " +
					client.getInetAddress().getHostName());
			
			BufferedReader inFromClient;
			
			try {
				inFromClient = new BufferedReader(
						new InputStreamReader(client.getInputStream()));
				
				String clientData = StringUtils.EMPTY;
				
				while((clientData = inFromClient.readLine()) != null) {
					LOG.info("[" + client.getInetAddress().getHostName() + "] " + 
						"Received from client - " + clientData);
				}
				
				// client socket was closed -> remove client from list
				clients.remove(client);
			} catch (IOException e) {
				LOG.error(e);
			}
			
			System.out.println("Disconnected from client : " +
					client.getInetAddress().getHostName());
		}
	}
}