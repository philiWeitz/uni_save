package org.uta.tcp.client;

import jssc.SerialPortException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public abstract class DtrRtsThread implements Runnable  {
	private static Logger LOG = LogManager.getLogger(DtrRtsThread.class);
	
	private boolean running = true;				
	private boolean active = true;	
	
	private TCPClient tcpClient;
		
	protected abstract void setBitActive(boolean active) throws SerialPortException;
	protected abstract int getPeriodInMs();
	protected abstract String getLogTag(); 	
	
	
	public DtrRtsThread() {
		tcpClient = new TCPClient();
	}
	
	
	public void run() {			
		while(running) {
			try {	
				setBitActive(active);
				tcpClient.sendDataToServer(getLogTag() + " set to " + active);
				
				Thread.sleep(getPeriodInMs());
				active = !active;	
				
			} catch (SerialPortException e) {
				LOG.error("Error while setting bit to " + active);
			} catch (InterruptedException e) {
				LOG.error("Error while putting Thread to sleep");
			}
		}
		
		try {
			setBitActive(false);
		} catch (SerialPortException e) {
			LOG.error("Error while setting bit to " + active);
		}
	}
	
	
	public void stopThread() {
		running = false;
	}
}