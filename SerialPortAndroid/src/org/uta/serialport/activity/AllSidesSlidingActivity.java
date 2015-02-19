package org.uta.serialport.activity;

import org.uta.serialport.ftdi.SerialPortUtil;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class AllSidesSlidingActivity extends Activity {
	
	private View areaLeft;
	private View areaRight;
	private View areaTop;	
	private View areaBottom;	
	private TextView touchCoordinatesView;
	
	private SerialPortUtil serialPortUtil;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_sides_sliding);
		
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
    protected void onResume() {
        super.onResume();
        serialPortUtil.startGpioConnection();
    }

    
    @Override
    protected void onPause() {
    	serialPortUtil.stopConnection();
        super.onPause();
    }
   
    
	@Override
    public boolean onTouchEvent(MotionEvent event) {
			
		if(event.getAction() == android.view.MotionEvent.ACTION_UP) {
			// stop all vibration
			serialPortUtil.clearAllGpioPins();
			touchCoordinatesView.setText("Touch released");
		} else if(isTouchingView(event,areaLeft)) {
			serialPortUtil.driveGpioPins(SerialPortUtil.MOVE_LEFT);
			touchCoordinatesView.setText("To the left - X: " + event.getRawX() + " - Y: " + event.getRawY());
		} else if(isTouchingView(event,areaRight)) {
			serialPortUtil.driveGpioPins(SerialPortUtil.MOVE_RIGHT);
			touchCoordinatesView.setText("To the right - X: " + event.getRawX() + " - Y: " + event.getRawY());
		} else if(isTouchingView(event,areaTop)) {
			serialPortUtil.driveGpioPins(SerialPortUtil.MOVE_UP);
			touchCoordinatesView.setText("To the top - X: " + event.getRawX() + " - Y: " + event.getRawY());
		} else if(isTouchingView(event,areaBottom)) {
			serialPortUtil.driveGpioPins(SerialPortUtil.MOVE_DOWN);
			touchCoordinatesView.setText("To the bottom - X: " + event.getRawX() + " - Y: " + event.getRawY());
		} else {
			serialPortUtil.clearAllGpioPins();
			touchCoordinatesView.setText("Not touch area");
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
