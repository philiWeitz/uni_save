package org.uta.tcp.leapmotion;

import org.uta.tcp.client.ServerCommand;
import org.uta.tcp.client.TcpClient;
import org.uta.tcp.jni.KeyboardUtil;


public class KeyboardController implements Runnable {
	private static KeyboardController instance;
	
	
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
			
			running = true;
			alreadyPressed = new boolean[256];

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

			keyToCommandMapping(keys, KeyboardVirtualKey.W, ServerCommand.Previous);
			keyToCommandMapping(keys, KeyboardVirtualKey.S, ServerCommand.Next);
			keyToCommandMapping(keys, KeyboardVirtualKey.A, ServerCommand.PreviousScreen);
			keyToCommandMapping(keys, KeyboardVirtualKey.D, ServerCommand.NextScreen);
			keyToCommandMapping(keys, KeyboardVirtualKey.Q, ServerCommand.Select);
			keyToCommandMapping(keys, KeyboardVirtualKey.Z, ServerCommand.Nack, "Half Pressed");
			keyToCommandMapping(keys, KeyboardVirtualKey.X, ServerCommand.Nack, "Full Pressed");
		}
	}
		
	
	private void keyToCommandMapping(short[] keys, int key, ServerCommand command, String... arg) {
		if(keys[key] < 0 && !alreadyPressed[key]) {
			TcpClient.getInstance().sendCommand(command, arg);
			alreadyPressed[key] = true;
		} else if(keys[key] >= 0) {
			alreadyPressed[key] = false;
		}
	}
}
