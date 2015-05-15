package org.uta.tcp.client;

import org.uta.tcp.client.SerialPortController.PORT;

public class TcpUtil {

	private TcpUtil() {

	}

	public static final int TCP_PORT = 6542;
	public static final String SERVER_ADDRESS = "10.0.0.4";
	//public static final String SERVER_ADDRESS = "153.1.64.90";
	
	public static final PORT dtrPort = PORT.COM3;
	public static final PORT rtsPort = PORT.COM3;	
	public static final PORT dataPort = PORT.COM3;	
}
