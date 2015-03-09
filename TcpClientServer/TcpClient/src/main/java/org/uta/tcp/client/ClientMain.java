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
						SerialPortController.getPortInstance(TcpUtil.rtsPort).setRtsPulse();
						TcpClient.getInstance().sendMessage("RTS");						
						break;
					case 2: 
						SerialPortController.getPortInstance(TcpUtil.dtrPort).setDtrPulse();
						TcpClient.getInstance().sendMessage("DTR");
						break;
					case 3: 
						SerialPortController.getPortInstance(TcpUtil.dataPort).sendData();
						TcpClient.getInstance().sendMessage("HC");
						break;						
					case 4:
						SerialPortController.closePortInstance(TcpUtil.dtrPort);
						SerialPortController.closePortInstance(TcpUtil.rtsPort);
						SerialPortController.closePortInstance(TcpUtil.dataPort);	
						TcpClient.getInstance().disconnectFromServer();
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
}
