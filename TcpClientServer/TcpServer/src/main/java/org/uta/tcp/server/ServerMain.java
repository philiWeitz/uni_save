package org.uta.tcp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.uta.tcp.client.ServerCommand;
import org.uta.tcp.client.TcpUtil;

public class ServerMain {
	private static Logger LOG = LogManager.getLogger(ServerMain.class);
	private static List<Socket> clients = new ArrayList<Socket>();

	
	private static ServerSocket server = null;
	
	
	public static void main(String args[]) throws IOException, InterruptedException {
						
		Thread clientListener = new Thread(new ClientListenerThread());
		clientListener.setName("Client Listener");
		clientListener.setPriority(Thread.NORM_PRIORITY);
		clientListener.start();
		
		while(true) {
			System.out.println("Please select an option:");
			System.out.println("1. RTS");
			System.out.println("2. DTR");
			System.out.println("3. HC");
			System.out.println("4. End");
			
			try {
				InputStreamReader sr =new InputStreamReader(System.in);
				BufferedReader br=new BufferedReader(sr);

				int input = Integer.parseInt(br.readLine());
			
				switch(input) {
					case 1: 
						sendCommandToClients(ServerCommand.Rts);
						break;
					case 2: 
						sendCommandToClients(ServerCommand.Dtr);
						break;
					case 3: 
						sendCommandToClients(ServerCommand.Hc);
						break;
					case 4: 
						if(null != server) {
							server.close();
						}
						clientListener.join();
						return;						
					default: 						
						break;
				}
			} catch(NumberFormatException e) {
				System.out.println("Please type in a valid integer number");
			} catch (IOException e) {
				LOG.error("Error while reading user input", e);
			}	
		}
	}
	
	
	private static void sendCommandToClients(ServerCommand command) {
		for(Socket client : clients) {

			String msg = ServerCommand.createServerCommand(command, StringUtils.EMPTY);
			
			try {
				PrintWriter outToServer = new PrintWriter(client.getOutputStream(),true);
				outToServer.println(msg);
				
				LOG.info("Sending command \"" + msg + "\" to client \"" + client.getInetAddress().getHostName() + "\"");
			} catch (IOException e) {
				LOG.error("Error sending command to \"" + client.getInetAddress().getHostName() + "\"", e);
			}	
		}
	}
	

	static class ClientListenerThread implements Runnable {

		public void run() {

			try {
				server = new ServerSocket(TcpUtil.TCP_PORT);
				
				while (true) {
					System.out.println("Waiting for clients...");
					
					Socket client = server.accept();
					clients.add(client);
					
					Thread clientThread = new Thread(new ServerThread(client));
					clientThread.setName(client.getInetAddress().getHostAddress());
					clientThread.start();
				}
			} catch (Exception e) {
				if(null != server && server.isClosed()) {
					System.out.println("Shutting down the server");
				} else {
					LOG.error(e);
				}
			}
		}
	};
	
	
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
					LOG.info("Received from client - " + clientData);
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