package org.uta.tcp.client;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jssc.SerialPort;
import jssc.SerialPortException;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class SerialPortController {
	private static Logger LOG = LogManager.getLogger(SerialPortController.class);
	
	
	private static Map<PORT, SerialPortController> ports = 
			new HashMap<PORT, SerialPortController>(); 
	
	
	private static final int DTR_SLEEP = 100;
	private static final int RTS_SLEEP = 100;
	
			
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
	private SerialPort serialPort;
	
	private ExecutorService executor;
	
	
	private SerialPortController(PORT port) {
		this.port = port;
			
		BasicThreadFactory factory = new BasicThreadFactory.Builder()
	     	.namingPattern("Serial Port")
	     	.daemon(true)
	     	.priority(Thread.MIN_PRIORITY)
	     	.build();
		 
		executor = Executors.newCachedThreadPool(factory);
	}
	
		
	public void setDtrPulse() {
		executor.execute(new DtrThread());
	}
	
	
	public void setRtsPulse() {
		executor.execute(new RtsThread());
	}

		
	public void sendData() {
		executor.execute(new sendDataThread());
	}
	
	
	private boolean connectToSerialPort() {		

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
        
		LOG.debug("Serial port " + port + " opened");
		
        return true;
	}
	
		
	private void closeConnection() {
		if(null != serialPort && serialPort.isOpened()) {
            try {
				serialPort.closePort();
				LOG.debug("Serial port " + port + " closed");
			} catch (SerialPortException e) {
				LOG.error("Error while closing serial port " + port);
			}
		}
	}
	
	
	private class DtrThread implements Runnable {
		public void run() {
			
			LOG.debug("Creating DTR pulse");

			try {

				// 2 pulses
				for(int i = 0; i < 2; ++i) {
										
					serialPort.setDTR(true);
					Thread.sleep(DTR_SLEEP);					
					serialPort.setDTR(false);
					Thread.sleep(DTR_SLEEP);
				}
				
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

			LOG.debug("Creating RTS pulse");

			try {

				// 2 pulses
				for(int i = 0; i < 2; ++i) {					
					serialPort.setRTS(true);
					Thread.sleep(RTS_SLEEP);				
					serialPort.setRTS(false);
					Thread.sleep(RTS_SLEEP);
				}
				
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

			LOG.debug("Creating data pulse");

			try {

				serialPort.writeString("M0");
				serialPort.writeString("F0,0,100");	

			} catch (SerialPortException e) {
				LOG.error("Error sending data: couldn't access serial port " + port);
			} catch (NullPointerException e) {
				LOG.error("Error: not connected to serial port " + port);	
			}
		}
	}
}
