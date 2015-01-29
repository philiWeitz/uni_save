package org.uta.serialport.ftdi;

import android.content.Context;


public class SerialPortUtil {
	
	public static final byte ALL_PINS_AS_BYTE = 0xf;
	public static final byte NO_PINS_AS_BYTE = 0x0;
	
	// representing pin lists (0 = GPIO0, 1 = GPIO1, ...)
	public static final int[] MOVE_UP = new int[] {0};
	public static final int[] MOVE_DOWN = new int[] {0,1};
	public static final int[] MOVE_LEFT = new int[] {2};
	public static final int[] MOVE_RIGHT = new int[] {2,3};
	
	
	private byte currentDriving = 0;
	private FT311GPIOInterface fpgiInterface;
	
	
	public SerialPortUtil(Context context) {
		fpgiInterface = new FT311GPIOInterface(context);
	}
	
	
	public void stopConnection() {
		fpgiInterface.DestroyAccessory();
	}
	
	
	public void startConnection() {
		// establish the connection
		fpgiInterface.ResumeAccessory();
		// set all pins into out mode
		fpgiInterface.ConfigPort(ALL_PINS_AS_BYTE, NO_PINS_AS_BYTE);
		// set all pins to low
		clearAllPins();
	}
	
	
	public void clearAllPins() {
		drivePins(new int[] {});
	}
	
	
	public void drivePins(int... pins) {
		byte mask = 0;
		
		for(int pin : pins) {
			mask |= 1 << pin;
		}
		
		if(mask != currentDriving) {
			currentDriving = mask;
			fpgiInterface.WritePort(mask);
		}
	}
}
