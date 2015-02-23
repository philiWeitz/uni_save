package org.uta.tcp.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ClientMain {
	private static Logger LOG = LogManager.getLogger(ClientMain.class);
	
	public static void main(String[] args) {
		
		init();
		mainMenu();
	}
	
	
	private static void init() {
		SerialPortController.getInstance().connectToSerialPort();
	}
	
	
	private static void mainMenu() {
		
		while(true) {
			System.out.println("Please select an option:");
			System.out.println("1. RTS");
			System.out.println("2. DTS");
			System.out.println("3. End");
			
			try {
				InputStreamReader sr =new InputStreamReader(System.in);
				BufferedReader br=new BufferedReader(sr);

				int input = Integer.parseInt(br.readLine());
			
				switch(input) {
					case 1: 
						SerialPortController.getInstance().startRtsThread();
						break;
					case 2: 
						SerialPortController.getInstance().startDtrThread();
						break;
					default: 
						SerialPortController.getInstance().closeConnection();
						return;
				}
			} catch(NumberFormatException e) {
				System.out.println("Please type in a valid integer number");
			} catch (IOException e) {
				LOG.error("Error while reading user input", e);
			}		
		}
	}
}
