package org.uta.tcp.client;

import java.util.HashMap;
import java.util.Map;

import jssc.SerialPort;
import jssc.SerialPortException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class SerialPortController {
	private static Logger LOG = LogManager.getLogger(SerialPortController.class);
	
	
	private static Map<PORT, SerialPortController> ports = 
			new HashMap<PORT, SerialPortController>(); 
	
	
	private static final int DTR_SLEEP = 500;
	private static final int RTS_SLEEP = 500;
	
			
	public enum PORT {
		COM1,
		COM2,
		COM3
	}
		
	
	public static SerialPortController getPortInstance(PORT port) {
		if(!ports.containsKey(port)) {
			
			// connect to port
			SerialPortController controller = new SerialPortController(port);
			controller.connectToSerialPort();
			
			// prevents a port from getting opened multiple times 
			ports.put(port, controller);
		}
		
		return ports.get(port);
	}
	
	
	public static void closePortInstance(PORT port) {
		if(ports.containsKey(port)) {
			
			SerialPortController controller = ports.get(port);
			controller.closeConnection();
			
			ports.remove(port);
		}
	}
	
	
	
	
	/*********************************************************************************************/	
	
	
	
	private PORT port;
	private TcpClient tcpClient;
	private SerialPort serialPort;
	

	private SerialPortController(PORT port) {
		this.port = port;
	}
	
		
	public void setDtrPulse() {
		startPortThread(new DtrThread());
	}
	
	
	public void setRtsPulse() {
		startPortThread(new RtsThread());
	}

		
	public void sendData() {
		startPortThread(new sendDataThread());
	}
	
	
	private boolean connectToSerialPort() {		
		tcpClient = new TcpClient();
		tcpClient.connectToServer();
		
        serialPort = new SerialPort(port.toString());
        
        try {
            serialPort.openPort();
            
            serialPort.setParams(SerialPort.BAUDRATE_9600, 
                                 SerialPort.DATABITS_8,
                                 SerialPort.STOPBITS_1,
                                 SerialPort.PARITY_NONE,
                                 false,
                                 false);

        } catch (SerialPortException ex) {
            LOG.error("Can't connect to serial port " + port);
            
            return false;
        }
        
        return true;
	}
	
		
	private void closeConnection() {
		if(null != serialPort && serialPort.isOpened()) {
            try {
				serialPort.closePort();
				tcpClient.disconnectFromServer();
			} catch (SerialPortException e) {
				LOG.error("Error while closing serial port " + port);
			}
		}
	}
	
	
	private void startPortThread(Runnable run) {

		Thread thread = new Thread(run);
		thread.setPriority(Thread.NORM_PRIORITY);
		thread.setName("Serial Port Control");
		thread.start();
	}

	
	private class DtrThread implements Runnable {
		public void run() {

			try {
			
				tcpClient.sendMessage("Activate DTR pulse");

				// 2 pulses
				for(int i = 0; i < 2; ++i) {
										
					serialPort.setDTR(true);
					Thread.sleep(DTR_SLEEP);					
					serialPort.setDTR(false);
					Thread.sleep(DTR_SLEEP);
				}
				
				tcpClient.sendMessage("Deactivate DTR pulse");
				
			} catch (InterruptedException e) {
				LOG.error("Error DTR Pulse: couldn't send thread to sleep");
			} catch (SerialPortException e) {
				LOG.error("Error DTR Pulse: couldn't access serial port " + port);
			} catch (NullPointerException e) {
				LOG.error("Error: not connected to serial port " + port);	
			}
		}
	}
	
	
	private class RtsThread implements Runnable {
		public void run() {

			try {

				tcpClient.sendMessage("Activate RTS pulse");
			
				// 2 pulses
				for(int i = 0; i < 2; ++i) {					
					serialPort.setRTS(true);
					Thread.sleep(RTS_SLEEP);				
					serialPort.setRTS(false);
					Thread.sleep(RTS_SLEEP);
				}
				
				tcpClient.sendMessage("Deactivate RTS pulse");
				
			} catch (InterruptedException e) {
				LOG.error("Error RTS Pulse: couldn't send thread to sleep");
			} catch (SerialPortException e) {
				LOG.error("Error RTS Pulse: couldn't access serial port " + port);
			} catch (NullPointerException e) {
				LOG.error("Error: not connected to serial port " + port);	
			}
		}
	}
	
	
	private class sendDataThread implements Runnable {
		public void run() {

			try {
				
				tcpClient.sendMessage("Sending data (HC)");
				
				serialPort.writeString("M0");
				serialPort.writeString("F0,0,100");	
			
				tcpClient.sendMessage("Sending done (HC)");
				
			} catch (SerialPortException e) {
				LOG.error("Error sending data: couldn't access serial port " + port);
			} catch (NullPointerException e) {
				LOG.error("Error: not connected to serial port " + port);	
			}
		}
	}
}
