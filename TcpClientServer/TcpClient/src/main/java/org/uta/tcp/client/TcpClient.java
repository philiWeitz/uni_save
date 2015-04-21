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


public class TcpClient {

	private static Logger LOG = LogManager.getLogger(TcpClient.class);
	private static TcpClient instance;
	
	
	private Socket clientSocket;
	private PrintWriter outToServer;
	private boolean connected = false;
	
	
	public static TcpClient getInstance() {
		if(null == instance) {
			instance = new TcpClient();
		}
		
		return instance;
	}
	
	
	private TcpClient() {
		
	}
	
		
	public boolean connectToServer() {
		try {
			
			LOG.info("Trying to connect to " + TcpUtil.SERVER_ADDRESS);

			clientSocket = new Socket(TcpUtil.SERVER_ADDRESS,
					TcpUtil.TCP_PORT);			

			LOG.info("Connected to " + TcpUtil.SERVER_ADDRESS);
			
			outToServer = new PrintWriter(clientSocket.getOutputStream(),true);			

			Thread thread = new Thread(new ServerListener(clientSocket));
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.setName("TCP Server read");
			thread.start();
			
			connected = true;
			
			return true;
		} catch (UnknownHostException e) {
			LOG.error("Can't connect to " + TcpUtil.SERVER_ADDRESS, e);
		} catch (IOException e) {
			LOG.error("Error: can't connect to \"" + TcpUtil.SERVER_ADDRESS + "\"");
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
	
	
	public void sendCommand(ServerCommand command, String body) {
		String msg = ServerCommand.createServerCommand(command, body);		
		LOG.info(msg);
		
		if(connected) {
			outToServer.println(msg);
		} else {
			LOG.error("TCP Error: Not connected to log server");
		}
	}
	
	
	public void sendCommand(ServerCommand command) {
		sendCommand(command, StringUtils.EMPTY);
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
					
					ServerCommand command = ServerCommand.serverStringToCommand(clientData);
					
					switch (command) {
						case Next:
							SerialPortController.getPortInstance(TcpUtil.dtrPort).setDtrPulse();
							SerialPortController.getPortInstance(TcpUtil.rtsPort).setRtsPulse();							
							break;
						case NextScreen:
							SerialPortController.getPortInstance(TcpUtil.dtrPort).setDtrPulse();
							SerialPortController.getPortInstance(TcpUtil.rtsPort).setRtsPulse();
							break;
						case Previous:
							SerialPortController.getPortInstance(TcpUtil.dtrPort).setDtrPulse();
							SerialPortController.getPortInstance(TcpUtil.rtsPort).setRtsPulse();
							break;
						case PreviousScreen:
							SerialPortController.getPortInstance(TcpUtil.dtrPort).setDtrPulse();
							SerialPortController.getPortInstance(TcpUtil.rtsPort).setRtsPulse();
							break;
						case Select:
							SerialPortController.getPortInstance(TcpUtil.dtrPort).setDtrPulse();
							SerialPortController.getPortInstance(TcpUtil.rtsPort).setRtsPulse();
							break;
						case SelectScreen:
							SerialPortController.getPortInstance(TcpUtil.dtrPort).setDtrPulse();
							SerialPortController.getPortInstance(TcpUtil.rtsPort).setRtsPulse();
							break;
						case Ack:
							ServerCommand.parseCommandCode(clientData);
							break;
						case Error:
							ServerCommand.parseCommandCode(clientData);
							break;
						case Invalid_Command:
							break;
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
