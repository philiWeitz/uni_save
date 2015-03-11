package org.uta.move.activity;

import org.uta.move.R;
import org.uta.move.view.DrawView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainActivity extends Activity {
	
	private DrawView drawView;
	
	private Button drawingControl;
	private Button motionControl;
	
	private boolean drawingActive = false;
	private boolean motionActive = false;	
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initUI();
    }
    
    
    private void initUI() {
    	drawView = (DrawView) findViewById(R.id.drawview_main);
    	
    	drawingControl = (Button) findViewById(R.id.button_draw_control);
    	motionControl = (Button) findViewById(R.id.button_motion_control);
    	
    	drawingControl.setOnClickListener(onDrawingControlClick);
    	motionControl.setOnClickListener(onMotionControlClick);
    }
    
   
	private OnClickListener onDrawingControlClick = new OnClickListener() {	
		@Override
		public void onClick(View v) {
			motionActive = false;
			drawingActive = !drawingActive;
			drawView.setDrawingActive(drawingActive);
			
			if(drawingActive) {
				drawingControl.setText(R.string.main_activity_drawing_active);		
			} else {
				drawingControl.setText(R.string.main_activity_drawing_deactive);
			}
			
			motionControl.setText(R.string.main_activity_motion_deactive);
		}
	};
	
	
	private OnClickListener onMotionControlClick = new OnClickListener() {	
		@Override
		public void onClick(View v) {
			drawingActive = false;
			motionActive = !motionActive;
			drawView.setMotionActive(motionActive);
			
			if(motionActive) {
				motionControl.setText(R.string.main_activity_motion_active);
			} else {
				motionControl.setText(R.string.main_activity_motion_deactive);
			}
			
			drawingControl.setText(R.string.main_activity_drawing_deactive);
		}
	};
}
