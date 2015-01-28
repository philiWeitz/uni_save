package org.uta.serialport.activity;

import org.uta.serialport.ftdi.SerialPortUtil;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class OneSideSlidingActivity extends Activity {

	private SerialPortUtil serialPortUtil;
	
	private Handler handler = new Handler();	
	private NumberPicker timePicker;

	
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
	}


	public void onRadioButtonClicked(View view) {
				
		if(((RadioButton) view).isChecked()) {   
			
		    switch(view.getId()) {
		        case R.id.one_side_sliding_no_sliding:
		        	serialPortUtil.clearAllPins();
		            break;
		        case R.id.one_side_sliding_top_sliding:
		        	serialPortUtil.drivePins(SerialPortUtil.MOVE_UP);
		            break;
		        case R.id.one_side_sliding_bottom_sliding:
		        	serialPortUtil.drivePins(SerialPortUtil.MOVE_DOWN);
		            break;
		        case R.id.one_side_sliding_left_sliding:
		        	serialPortUtil.drivePins(SerialPortUtil.MOVE_LEFT);
		            break;
		        case R.id.one_side_sliding_right_sliding:
		        	serialPortUtil.drivePins(SerialPortUtil.MOVE_RIGHT);
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
	protected void onResume() {
		super.onResume();
		serialPortUtil.startConnection();
	}

	@Override
	protected void onPause() {
		serialPortUtil.stopConnection();
		super.onPause();
	}
}
