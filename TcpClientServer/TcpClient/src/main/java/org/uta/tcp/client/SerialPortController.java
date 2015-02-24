package org.uta.tcp.client;

import jssc.SerialPort;
import jssc.SerialPortException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class SerialPortController implements Runnable {
	private static Logger LOG = LogManager.getLogger(SerialPortController.class);
	
	private static final int DTR_SLEEP = 1000;
	private static final int RTS_SLEEP = 500;
	
	
	private static boolean running = false;
	private static SerialPortController instance;
	
	private static final String COM_PORT = "COM3";

	
	private TCPClient tcpClient;
	private SerialPort serialPort;
	private boolean signalLevel = true;
	private CONTROL_BIT active = CONTROL_BIT.DTR;
	

	private enum CONTROL_BIT {
		DTR,
		RTS
	};
	
	
	private SerialPortController() {
		tcpClient = new TCPClient();
		tcpClient.connectToServer();
	}
	
	
	public static void setDtrActive() {
		if(running && null != instance) {
			instance.setDTR();
		} else {
			LOG.error("Error: Not connected to serial port!");
		}
	}
	
	
	public static void setRtsActive() {
		if(running && null != instance) {
			instance.setRTS();
		} else {
			LOG.error("Error: Not connected to serial port!");
		}
	}
	
	
	public static void startControlThread() {
		if(!running) {
			running = true;
			
			instance = new SerialPortController();
			Thread thread = new Thread(instance);
			thread.setPriority(Thread.NORM_PRIORITY);
			thread.setName("Serial Port Control");
			thread.start();
		}
	}
	
	
	public static void stopControlThread() {
		running = false;
		instance = null;
	}
	
		
	public void run() {
		connectToSerialPort();
		
		while(running) {
			try {
				if(active == CONTROL_BIT.DTR) {
					serialPort.setDTR(signalLevel);
					Thread.sleep(DTR_SLEEP);			
				} else {
					serialPort.setRTS(signalLevel);
					Thread.sleep(RTS_SLEEP);
				}
				
				signalLevel = !signalLevel;
				
			} catch (SerialPortException e) {
				LOG.error("Error setting DTR/RTS", e);
			} catch (InterruptedException e) {
				LOG.error("Error sending thread to sleep", e);
			}
		}

		closeConnection();
	};
	
		
	public void connectToSerialPort() {
		
        serialPort = new SerialPort(COM_PORT);
        
        try {
            serialPort.openPort();
            
            serialPort.setParams(SerialPort.BAUDRATE_9600, 
                                 SerialPort.DATABITS_8,
                                 SerialPort.STOPBITS_1,
                                 SerialPort.PARITY_NONE,
                                 false,
                                 false);

        } catch (SerialPortException ex) {
            LOG.error("Can't connect to serial port " + COM_PORT);
            running = false;
        }
	}
	
		
	public void closeConnection() {
		if(null != serialPort && serialPort.isOpened()) {
            try {
				serialPort.closePort();
				tcpClient.disconnectFromServer();
			} catch (SerialPortException e) {
				LOG.error("Error while closing serial port " + COM_PORT);
			}
		}
	}
	
	
	public void setDTR() {
		active = CONTROL_BIT.DTR;
		
		try {
			serialPort.setRTS(false);
			LOG.info("Setting DTR");
		} catch (SerialPortException e) {
			LOG.error("Error setting DTR", e);
		}
		
		tcpClient.sendMessage("Set DTR");
	}
	
	
	public void setRTS() {
		active = CONTROL_BIT.RTS;
		
		try {
			serialPort.setDTR(false);
			LOG.info("Setting RTS");
		} catch (SerialPortException e) {
			LOG.error("Error setting RTS", e);
		}
		
		tcpClient.sendMessage("Set RTS");
	}
}
