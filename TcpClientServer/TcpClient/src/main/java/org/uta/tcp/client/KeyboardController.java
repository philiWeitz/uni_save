package org.uta.tcp.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.uta.tcp.jni.KeyboardUtil;


public class KeyboardController implements Runnable {
	private static Logger LOG = LogManager.getLogger(KeyboardController.class);	
	
	// Virtual key codes can be found at:
	// https://msdn.microsoft.com/en-us/library/windows/desktop/dd375731%28v=vs.85%29.aspx
	private static final int COMMAND_A = 0x41;
	private static final int COMMAND_K = 0x4B;
	private static final int KEY_READ_TIMEOUT = 100;

	private static KeyboardController instance;
	
	
	public static KeyboardController getInstance() {
		if(null == instance) {
			instance = new KeyboardController();
		}
		
		return instance;
	}
	
	
	/**********************************************************************/
	
	private boolean running = false;
	
	
	private KeyboardController() {
		
	}
	
	
	public void start() {
		if(!running) {
			running = true;
			
			Thread thread = new Thread(this);
			thread.setPriority(Thread.NORM_PRIORITY);
			thread.setName("Keyboard Controller");
			thread.start();
		}
	}
	
	
	public void stop() {
		running = false;
	}
	
	
	@Override
	public void run() {
		while(running) {
			short[] keys = KeyboardUtil.getKeyboardState();
			
			if(keys[COMMAND_A] != 0) {
				System.out.println("A Pressed");
			}
			if(keys[COMMAND_K] != 0) {
				System.out.println("K Pressed");
			}
			
			try {
				Thread.sleep(KEY_READ_TIMEOUT);
			} catch (InterruptedException e) {
				LOG.error(e);
			}
		}
	}
}
