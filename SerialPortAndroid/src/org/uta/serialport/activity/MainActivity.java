package org.uta.serialport.activity;

import org.uta.serialport.ftdi.SerialPortUtil;
import org.uta.serialportandroid.R;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	
	private View areaLeft;
	private View areaRight;
	private View areaTop;	
	private View areaBottom;	
	private TextView touchCoordinatesView;
	
	private SerialPortUtil serialPortUtil;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		init();
		initUI();
		startAnimations();
	}

	
	private void init() {
		serialPortUtil = new SerialPortUtil(this);
	}
	
	
	private void initUI() {
		areaLeft = (View) findViewById(R.id.area_to_left);
		areaRight = (View) findViewById(R.id.area_to_right);	
		areaTop = (View) findViewById(R.id.area_to_top);
		areaBottom = (View) findViewById(R.id.area_to_bottom);
		
		touchCoordinatesView = (TextView) findViewById(R.id.touch_coordinates);
	}
	
	
	private void startAnimations() {
		ImageView arrowLeft = (ImageView) findViewById(R.id.arrow_to_left_animation);
		AnimatorSet arrowLeftSet = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.arrow_to_left_animation);
		arrowLeftSet.setTarget(arrowLeft);
		arrowLeftSet.start();
		
		ImageView arrowRight = (ImageView) findViewById(R.id.arrow_to_right_animation);
		AnimatorSet arrowRightSet = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.arrow_to_right_animation);
		arrowRightSet.setTarget(arrowRight);
		arrowRightSet.start();
		
		ImageView arrowTop = (ImageView) findViewById(R.id.arrow_to_top_animation);
		AnimatorSet arrowTopSet = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.arrow_to_top_animation);
		arrowTopSet.setTarget(arrowTop);
		arrowTopSet.start();
		
		ImageView arrowBottom = (ImageView) findViewById(R.id.arrow_to_bottom_animation);
		AnimatorSet arrowBottomSet = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.arrow_to_bottom_animation);
		arrowBottomSet.setTarget(arrowBottom);
		arrowBottomSet.start();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
    @Override
    protected void onResume() {
        super.onResume();
        serialPortUtil.startConnection();
        serialPortUtil.drivePins(SerialPortUtil.MOVE_UP);
    }

    
    @Override
    protected void onPause() {
    	serialPortUtil.stopConnection();
        super.onPause();
    }
   
    
	@Override
    public boolean onTouchEvent(MotionEvent event) {
			
		if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
			// stop all vibration
			touchCoordinatesView.setText("Touch released");
			serialPortUtil.clearAllPins();
		} else {		
			if(isTouchingView(event,areaLeft)) {
				touchCoordinatesView.setText("To the left - X: " + event.getRawX() + " - Y: " + event.getRawY());
				serialPortUtil.drivePins(SerialPortUtil.MOVE_LEFT);
			} else if(isTouchingView(event,areaRight)) {
				touchCoordinatesView.setText("To the right - X: " + event.getRawX() + " - Y: " + event.getRawY());
				serialPortUtil.drivePins(SerialPortUtil.MOVE_RIGHT);
			} else if(isTouchingView(event,areaTop)) {
				touchCoordinatesView.setText("To the top - X: " + event.getRawX() + " - Y: " + event.getRawY());
				serialPortUtil.drivePins(SerialPortUtil.MOVE_UP);
			}  else if(isTouchingView(event,areaBottom)) {
				touchCoordinatesView.setText("To the bottom - X: " + event.getRawX() + " - Y: " + event.getRawY());
				serialPortUtil.drivePins(SerialPortUtil.MOVE_DOWN);
			} else {
				touchCoordinatesView.setText("Not touch area");
			}
		}
		return super.onTouchEvent(event);
    }
	
	
	private boolean isTouchingView(MotionEvent event, View view) {
		final int X = 0;
		final int Y = 1;
		
		int[] areaCoordinates = new int[2]; 
		view.getLocationOnScreen(areaCoordinates);
		
		if(event.getX() >= areaCoordinates[X] && event.getX() <= (areaCoordinates[X] + view.getWidth()) &&
				event.getY() >= areaCoordinates[Y] && event.getY() <= (areaCoordinates[Y] + view.getHeight())) {
			return true;
		}
		return false;
	}
}
