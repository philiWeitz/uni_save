package org.uta.serialport.activity;

import org.uta.serialport.ftdi.SerialPortUtil;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class OneSideSlidingActivity extends Activity {

	private SerialPortUtil serialPortUtil;
	
	private Handler handler = new Handler();	
	private NumberPicker timePicker;
	private TextView infoField;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_one_side_sliding);

		initUI();
		serialPortUtil = new SerialPortUtil(this);
	}
	
	private void initUI() {
		timePicker = (NumberPicker) findViewById(R.id.one_side_sliding_time_picker);
		timePicker.setMaxValue(20);
		timePicker.setMinValue(0);
		
		infoField = (TextView) findViewById(R.id.one_side_sliding_pressure);
	}


	public void onRadioButtonClicked(View view) {
				
		if(((RadioButton) view).isChecked()) {   
			
		    switch(view.getId()) {
		        case R.id.one_side_sliding_no_sliding:
		        	serialPortUtil.clearAllGpioPins();
		            break;
		        case R.id.one_side_sliding_top_sliding:
		        	serialPortUtil.driveGpioPins(SerialPortUtil.MOVE_UP);
		            break;
		        case R.id.one_side_sliding_bottom_sliding:
		        	serialPortUtil.driveGpioPins(SerialPortUtil.MOVE_DOWN);
		            break;
		        case R.id.one_side_sliding_left_sliding:
		        	serialPortUtil.driveGpioPins(SerialPortUtil.MOVE_LEFT);
		            break;
		        case R.id.one_side_sliding_right_sliding:
		        	serialPortUtil.driveGpioPins(SerialPortUtil.MOVE_RIGHT);
		            break;
		        case R.id.one_side_sliding_pulses:
		        	serialPortUtil.driveGpioPins(SerialPortUtil.MOVE_UP);
		        	serialPortUtil.driveGpioPins(SerialPortUtil.MOVE_DOWN);
		        	serialPortUtil.driveGpioPins(SerialPortUtil.MOVE_LEFT);
		        	serialPortUtil.driveGpioPins(SerialPortUtil.MOVE_RIGHT);
		        	serialPortUtil.driveGpioPins(SerialPortUtil.MOVE_UP, SerialPortUtil.MOVE_DOWN);		        	
		            break;
		    }
		    
		    int executionTime = timePicker.getValue();

		    if(view.getId() != R.id.one_side_sliding_no_sliding && executionTime > 0) {
		    	
		    	handler.postDelayed(run, executionTime * 1000);
		    	enableRadioButtons(false);
		    }
		}
	}
	
	
	private Runnable run = new Runnable() {
		@Override
		public void run() {			
			enableRadioButtons(true);
			((RadioButton) findViewById(R.id.one_side_sliding_no_sliding)).performClick();
		}
	};

	
	private void enableRadioButtons(boolean enable) {
		RadioGroup group = (RadioGroup) findViewById(R.id.one_side_sliding_radio_group);
		for(int i = 0; i < group.getChildCount(); ++i) {
			group.getChildAt(i).setEnabled(enable);
		}
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {	
		
		if(event.getAction() == android.view.MotionEvent.ACTION_UP) {
			infoField.setText("");
		} else {
			infoField.setText("Pressure: " + event.getPressure() + "; Size: " + event.getSize());	
		}
			
		return super.onTouchEvent(event);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		serialPortUtil.startGpioConnection();
	}

	@Override
	protected void onPause() {
		serialPortUtil.stopConnection();
		super.onPause();
	}
}
