package org.uta.tcp.client;

import jssc.SerialPort;
import jssc.SerialPortException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class SerialPortController {
	private static Logger LOG = LogManager.getLogger(SerialPortController.class);
	
	private static final int DTR_SLEEP = 1000;
	private static final int RTS_SLEEP = 500;
	
	private static final String COM_PORT = "COM3";
	private static SerialPortController instance;
	
	
	private DtrRtsThread currentRunning;
	private SerialPort serialPort;
	
	
	public static SerialPortController getInstance() {
		if(null == instance) {
			instance = new SerialPortController();
		}
		return instance;
	}
	
	
	private SerialPortController() {
		
	}
	
	
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
            LOG.error("Error while connecting to serial port " + COM_PORT);
        }
	}
	
	
	public void closeConnection() {
		if(null != serialPort) {
            try {
				serialPort.closePort();
			} catch (SerialPortException e) {
				LOG.error("Error while closing serial port " + COM_PORT);
			}
		}
		
		if(null != currentRunning) {
			currentRunning.stopThread();
		}
	}
	

	public void startRtsThread() {
		if(null != currentRunning) {
			if(currentRunning instanceof RtsThread) {
				return;
			}
			
			currentRunning.stopThread();
		}

		currentRunning = new RtsThread();	
		new Thread(currentRunning).start();
	}
	
	
	public void startDtrThread() {
		if(null != currentRunning) {
			if(currentRunning instanceof DtrThread) {
				return;
			}
			
			currentRunning.stopThread();
		}
		
		currentRunning = new DtrThread();
		new Thread(currentRunning).start();
	}
	
	
	private class DtrThread extends DtrRtsThread {
		
		@Override
		protected void setBitActive(boolean active) throws SerialPortException {
			serialPort.setDTR(active);
		}

		@Override
		protected int getPeriodInMs() {
			return DTR_SLEEP;
		}

		@Override
		protected String getLogTag() {
			return "DTR";
		}
	};


	private class RtsThread extends DtrRtsThread {

		@Override
		protected void setBitActive(boolean active) throws SerialPortException {
			serialPort.setRTS(active);
		}

		@Override
		protected int getPeriodInMs() {
			return RTS_SLEEP;
		}

		@Override
		protected String getLogTag() {
			return "RTS";
		}
	};
}
