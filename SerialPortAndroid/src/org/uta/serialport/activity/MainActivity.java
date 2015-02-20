package org.uta.serialport.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initUI();
	}

	
	private void initUI() {
		Button slideAllSidesButton = (Button) findViewById(R.id.button_slide_all_sides);
		Button slideOneSideButton = (Button) findViewById(R.id.button_slide_one_side);
		Button pwmButton = (Button) findViewById(R.id.button_pwm);
		
		slideAllSidesButton.setOnClickListener(slideAllSidesClick);
		slideOneSideButton.setOnClickListener(slideOneSideClick);
		pwmButton.setOnClickListener(pwmClick);
	}

	
	private OnClickListener slideOneSideClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			startArctivity(OneSideSlidingActivity.class);
		}
	};
	

	private OnClickListener slideAllSidesClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			startArctivity(AllSidesSlidingActivity.class);
		}
	};
	
	
	private OnClickListener pwmClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			startArctivity(PwmActivity.class);
		}
	};
	
	
	private void startArctivity(Class<?> activity) {
		Intent activityIntent = new Intent(this, activity);
		startActivity(activityIntent);
	}
}
