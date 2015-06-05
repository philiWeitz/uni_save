package org.uta.tcp.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ClientMain {
	private static Logger LOG = LogManager.getLogger(ClientMain.class);	

	
	public static void main(String[] args) {
		mainMenu();
	}

	
	private static void mainMenu() {
		
		TcpClient.getInstance().connectToServer();
		
		while(true) {
			System.out.println("Please type a command (:q for exit):");
			
			try {
				InputStreamReader sr =new InputStreamReader(System.in);
				BufferedReader br=new BufferedReader(sr);

				String input = br.readLine();
				
				if(!input.isEmpty()) {
					ServerCommand command = ServerCommand.serverStringToCommand(input);
	
					if(input.equals(":q")) {
						SerialPortController.closePortInstance(TcpUtil.dtrPort);
						SerialPortController.closePortInstance(TcpUtil.rtsPort);
						SerialPortController.closePortInstance(TcpUtil.dataPort);
						
						TcpClient.getInstance().disconnectFromServer();
						return;
						
					} else if(ServerCommand.Invalid_Command != command) {
						TcpClient.getInstance().sendCommand(command);
					}
				}
			} catch (IOException e) {
				LOG.error("Error while reading user input", e);
			}		
		}
	}
}
