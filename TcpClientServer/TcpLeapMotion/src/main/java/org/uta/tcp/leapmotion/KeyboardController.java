package org.uta.tcp.leapmotion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.uta.tcp.client.SerialPortController;
import org.uta.tcp.client.ServerCommand;
import org.uta.tcp.client.TcpClient;
import org.uta.tcp.client.TcpUtil;
import org.uta.tcp.jni.KeyboardUtil;


public class KeyboardController implements Runnable {
	
	private static KeyboardController instance;

	private static final int THREAD_TIME_OUT = 100;
	
	private static Logger LOG = LogManager.getLogger(KeyboardController.class);	
	
	
	public static KeyboardController getInstance() {
		if(null == instance) {
			instance = new KeyboardController();
		}
		
		return instance;
	}
	
	
	/**********************************************************************/
	
	private boolean running = false;
	private boolean alreadyPressed[];
	
	
	private KeyboardController() {
		
	}
	
	
	public void start() {
		if(!running) {		
			LOG.debug("Starting KeyboardController thread...");
			
			running = true;
			alreadyPressed = new boolean[256];

			Thread thread = new Thread(this);
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.setName("Keyboard Controller");
			thread.start();
		}
	}
	
	
	public void stop() {
		running = false;
	}
	
	
	@Override
	public void run() {
		LOG.debug("KeyboardController thread started");

		while(running) {
			short[] keys = KeyboardUtil.getKeyboardState();

			keyToCommandMapping(keys, KeyboardVirtualKey.W, ServerCommand.Previous);
			keyToCommandMapping(keys, KeyboardVirtualKey.S, ServerCommand.Next);
			keyToCommandMapping(keys, KeyboardVirtualKey.A, ServerCommand.PreviousScreen);
			keyToCommandMapping(keys, KeyboardVirtualKey.D, ServerCommand.NextScreen);
			keyToCommandMapping(keys, KeyboardVirtualKey.Q, ServerCommand.Select);
			keyToCommandMapping(keys, KeyboardVirtualKey.Z, ServerCommand.Nack, "pedal 50%");
			keyToCommandMapping(keys, KeyboardVirtualKey.X, ServerCommand.Nack, "pedal 100%");
		
			try {
				Thread.sleep(THREAD_TIME_OUT);
			} catch (InterruptedException e) {
				LOG.error(e);
			}
		}
		
		LOG.debug("Stopping KeyboardController Thread");
	}
		
	
	private void keyToCommandMapping(short[] keys, int key, ServerCommand command, String... arg) {
		if(keys[key] < 0 && !alreadyPressed[key]) {
			
			// send the command to the server
			TcpClient.getInstance().sendCommand(command, arg);
			// create haptic feedback by setting the dtr pulse
			SerialPortController.getPortInstance(TcpUtil.dtrPort).setDtrPulse();
			// prevent from sending the command multiple times
			alreadyPressed[key] = true;
			
			LOG.debug("Key with the virtual code '" + key + "' was pressed");
			
		} else if(keys[key] >= 0) {
			alreadyPressed[key] = false;
		}
	}
}
