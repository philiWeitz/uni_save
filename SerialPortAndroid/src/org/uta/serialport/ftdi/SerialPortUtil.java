package org.uta.serialport.ftdi;

import android.content.Context;


public class SerialPortUtil {
	
	public static final byte ALL_PINS_AS_BYTE = 0xf;
	public static final byte NO_PINS_AS_BYTE = 0x0;
	
	// representing pin lists (0 = GPIO0, 1 = GPIO1, ...)
	public static final int MOVE_UP = 0;
	public static final int MOVE_DOWN = 1;
	public static final int MOVE_LEFT = 2;
	public static final int MOVE_RIGHT = 3;
	
	
	private byte currentDriving = 0;
	private FT311GPIOInterface fpgiGpioInterface;
	private FT311PWMInterface fpgiPwmInterface;
	
	
	public SerialPortUtil(Context context) {
		fpgiGpioInterface = new FT311GPIOInterface(context);
		fpgiPwmInterface = new FT311PWMInterface(context);
	}
	
	
	public void stopConnection() {
		fpgiGpioInterface.DestroyAccessory();
		fpgiPwmInterface.DestroyAccessory();
	}
	
	
	public void startGpioConnection() {
		// establish the connection
		fpgiGpioInterface.ResumeAccessory();
		// set all pins into out mode
		fpgiGpioInterface.ConfigPort(ALL_PINS_AS_BYTE, NO_PINS_AS_BYTE);
		// set all pins to low
		clearAllGpioPins();
	}
	
	
	public void clearAllGpioPins() {
		driveGpioPins(new int[] {});
	}
	
	
	public void driveGpioPins(int... pins) {
		byte mask = 0;
		
		for(int pin : pins) {
			mask |= 1 << pin;
		}
		
		if(mask != currentDriving) {
			currentDriving = mask;
			fpgiGpioInterface.WritePort(mask);
		}
	}
	
	
	/************************************************************************************/
	
	
	public void startPwmConnection(int period) {
		// establish the connection
		fpgiPwmInterface.ResumeAccessory();
		// set period
		fpgiPwmInterface.SetPeriod(period);
		// resets all PWM channels
		fpgiPwmInterface.Reset();
	}
	
	
	// sets the duty cycle (5% up to 100%)
	public void setPwmDutyCycle(byte pwmChannel, byte dutyCycle) {
		if(pwmChannel >= 0 && pwmChannel < 4) {
			fpgiPwmInterface.SetDutyCycle(pwmChannel, dutyCycle);
		}
	}
	
	
	public void setPwmPeriod(int period) {
		fpgiPwmInterface.SetPeriod(period);
	}
}
