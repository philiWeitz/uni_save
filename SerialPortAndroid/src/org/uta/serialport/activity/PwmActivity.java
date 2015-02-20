package org.uta.serialport.activity;

import org.uta.serialport.ftdi.SerialPortUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class PwmActivity extends Activity {

	private static final byte ACTUATOR_1 = 0;
	private static final byte ACTUATOR_2 = 1;
	private static final byte ACTUATOR_3 = 2;
	private static final byte ACTUATOR_4 = 3;
	
	private boolean actuatorActive1 = false;
	private boolean actuatorActive2 = false;	
	private boolean actuatorActive3 = false;
	private boolean actuatorActive4 = false;
	
	private SeekBar seekBar1;
	private SeekBar seekBar2;
	private SeekBar seekBar3;
	private SeekBar seekBar4;
	
	private NumberPicker numberPicker;
	
	private SerialPortUtil serialPortUtil;
		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pwm);
		
		init();
	}
	
	
	private void init() {
		serialPortUtil = new SerialPortUtil(this);
		
		numberPicker = (NumberPicker) findViewById(R.id.numberPicker_period);
		numberPicker.setMinValue(1);
		numberPicker.setMaxValue(100);
		
		numberPicker.setOnValueChangedListener(new OnValueChangeListener() {		
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				serialPortUtil.setPwmPeriod(numberPicker.getValue());
			}
		});
		
		seekBar1 = (SeekBar) findViewById(R.id.seekBar_dutycycle1);
		seekBar2 = (SeekBar) findViewById(R.id.seekBar_dutycycle2);
		seekBar3 = (SeekBar) findViewById(R.id.seekBar_dutycycle3);
		seekBar4 = (SeekBar) findViewById(R.id.seekBar_dutycycle4);	
		
		seekBar1.setOnSeekBarChangeListener(new DutyCycleChange() {			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				((TextView) findViewById(R.id.textView_actuator1))
					.setText(String.valueOf(seekBar.getProgress()));
				
				if(actuatorActive1) {
					serialPortUtil.setPwmDutyCycle(ACTUATOR_1, (byte) seekBar.getProgress()); 
				}	
			}
		});
		
		seekBar2.setOnSeekBarChangeListener(new DutyCycleChange() {			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				((TextView) findViewById(R.id.textView_actuator2))
					.setText(String.valueOf(seekBar.getProgress()));
				
				if(actuatorActive2) {
					serialPortUtil.setPwmDutyCycle(ACTUATOR_2, (byte) seekBar.getProgress()); 
				}
			}
		});
		
		seekBar3.setOnSeekBarChangeListener(new DutyCycleChange() {			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				((TextView) findViewById(R.id.textView_actuator3))
					.setText(String.valueOf(seekBar.getProgress()));
				
				if(actuatorActive3) {
					serialPortUtil.setPwmDutyCycle(ACTUATOR_3, (byte) seekBar.getProgress()); 
				}
			}
		});
		
		seekBar4.setOnSeekBarChangeListener(new DutyCycleChange() {			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				((TextView) findViewById(R.id.textView_actuator4))
					.setText(String.valueOf(seekBar.getProgress()));
				
				if(actuatorActive4) {
					serialPortUtil.setPwmDutyCycle(ACTUATOR_4, (byte) seekBar.getProgress()); 
				}
			}
		});
	}

	
	private void buttonPressed(byte actuator, boolean active, SeekBar seekbar, Button button) {
		if(active) {
			serialPortUtil.setPwmDutyCycle(actuator, (byte) 0);
			button.setText(R.string.button_not_active);
		} else {
			serialPortUtil.setPwmDutyCycle(actuator, (byte) seekbar.getProgress()); 
			button.setText(R.string.button_active);
		}
	}
	

	public void onActuator1Clicked(View view) {		
		buttonPressed(ACTUATOR_1, actuatorActive1, seekBar1, (Button) view);
		actuatorActive1 = !actuatorActive1;
	}
	
	
	public void onActuator2Clicked(View view) {
		buttonPressed(ACTUATOR_2, actuatorActive2, seekBar2, (Button) view);	
		actuatorActive2 = !actuatorActive2;
	}
	
	
	public void onActuator3Clicked(View view) {
		buttonPressed(ACTUATOR_3, actuatorActive3, seekBar3, (Button) view);	
		actuatorActive3 = !actuatorActive3;
	}
	
	
	public void onActuator4Clicked(View view) {
		buttonPressed(ACTUATOR_4, actuatorActive4, seekBar4, (Button) view);	
		actuatorActive4 = !actuatorActive4;
	}
	
	
	private abstract class DutyCycleChange implements OnSeekBarChangeListener {
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
	};
	
	
	@Override
	protected void onResume() {
		super.onResume();
		serialPortUtil.startPwmConnection(numberPicker.getValue());
	}
	

	@Override
	protected void onPause() {
		serialPortUtil.stopConnection();
		super.onPause();
	}
}
