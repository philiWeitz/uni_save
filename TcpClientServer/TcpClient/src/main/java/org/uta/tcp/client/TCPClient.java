package org.uta.tcp.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.uta.tcp.server.ServerUtil;


public class TCPClient {
	private static Logger LOG = LogManager.getLogger(TCPClient.class);

	private Socket clientSocket;
	private PrintWriter outToServer;
	private boolean connected = false;
	
		
	public boolean connectToServer() {
		try {
			clientSocket = new Socket(ServerUtil.SERVER_ADDRESS,
					ServerUtil.TCP_PORT);
			
			outToServer = new PrintWriter(clientSocket.getOutputStream(),true);			

			Thread thread = new Thread(new ServerListener(clientSocket));
			thread.setPriority(Thread.NORM_PRIORITY);
			thread.setName("TCP Server read");
			thread.start();
			
			connected = true;
			
			return true;
		} catch (UnknownHostException e) {
			LOG.error("Can't connect to server " + ServerUtil.SERVER_ADDRESS, e);
		} catch (IOException e) {
			LOG.error("Error: can't connect to server \"" + ServerUtil.SERVER_ADDRESS + "\"");
		}
		return false;
	}

	
	public void disconnectFromServer() {
		if (connected) {
			try {
				outToServer.close();
				clientSocket.close();
				connected = false;
				
			} catch (IOException e) {
				LOG.error("Error while disconnecting from server", e);
			}
		}
	}
	
	
	public void sendMessage(String msg) {
		if(connected) {
			outToServer.println(msg);
		} else {
			LOG.error("TCP Error: Not connected to server \"" + ServerUtil.SERVER_ADDRESS + "\"");
		}
	}
	
	
	private class ServerListener implements Runnable {
		private Socket clientSocket;
		
		
		public ServerListener(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}
		
		
		public void run() {
			
			BufferedReader inFromClient;
			
			try {
				inFromClient = new BufferedReader(
						new InputStreamReader(clientSocket.getInputStream()));
							
				String clientData = StringUtils.EMPTY;
				
				while((clientData = inFromClient.readLine()) != null) {
					LOG.info("Received from server - " + clientData);
							
					if("RTS".equals(clientData)) {
						SerialPortController.setRtsActive();
					} else if("DTR".equals(clientData)) {
						SerialPortController.setDtrActive();
					}
				}	

				System.out.println("Disconnect from server");
			} catch(SocketException e) {
				// server socket was closed
			} catch (Exception e) {
				LOG.error("Error reading commands from server", e);
			}
		}
	}
}
