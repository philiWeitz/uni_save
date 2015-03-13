package org.uta.move.view;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.uta.move.stickslip.StickSlipControl;
import org.uta.move.stickslip.Vector2D;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawView extends View {
		
	private StickSlipControl stickSlipControl;
	private Vector2D lastTouchCoordinates;

	private ScheduledExecutorService scheduler;
	
	private boolean drawingActive = false;
	private boolean motionActive = false;	
	
	private Path drawPath;
	private Paint drawPaint;
	private Paint canvasPaint;
	private Canvas drawCanvas;
	private Bitmap canvasBitmap;
	
	private Paint whitePaint;
	
	private List<Vector2D> pathList = new LinkedList<Vector2D>();
	
	
	public DrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init();
	}
	
	
	public List<Vector2D> getPath() {
		return pathList;
	}
	
	
	public void setDrawingActive(boolean active) {
		motionActive = false;
		drawingActive = active;
		
		if(null != scheduler) {
			scheduler.shutdownNow();
			scheduler = null;
		}
	}
	
	
	public void setMotionActive(boolean active) {
		drawingActive = false;
		motionActive = active;
		
		if(null != scheduler) {
			scheduler.shutdownNow();
			scheduler = null;
		}
		
		if(active) {
			scheduler = Executors.newSingleThreadScheduledExecutor();
			scheduler.scheduleWithFixedDelay(coordinateUpdater, 0, 1, TimeUnit.MILLISECONDS);
		} else {
			invalidate();
		}
	}
	
	
	private void init(){
		drawPath = new Path();
		
		drawPaint = new Paint();		
		drawPaint.setColor(Color.BLACK);
		drawPaint.setAntiAlias(true);
		drawPaint.setStrokeWidth(5);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);
			
		whitePaint = new Paint(drawPaint);
		whitePaint.setColor(Color.WHITE);
		whitePaint.setStrokeWidth(4);
	
		canvasPaint = new Paint(Paint.DITHER_FLAG);	
		
		stickSlipControl = new StickSlipControl();
	}
	
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		if(null != canvasBitmap) {
			canvasBitmap.recycle();
		}
		
		canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		drawCanvas = new Canvas(canvasBitmap);
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
		canvas.drawPath(drawPath, drawPaint);
	}
	
	
	@Override
	@SuppressLint("ClickableViewAccessibility")
	public boolean onTouchEvent(MotionEvent event) {
		
		// draw current position
		if(null != lastTouchCoordinates) {
			drawCanvas.drawPoint(lastTouchCoordinates.getX(), lastTouchCoordinates.getY(), whitePaint);
		}
		
		lastTouchCoordinates = new Vector2D(event.getX(), event.getY());
		drawCanvas.drawPoint(event.getX(), event.getY(), drawPaint);
		
		// perform the active action
		if(drawingActive) {
			drawPath(event);
		} else if(motionActive) {
			moveObject();
		}
		
		invalidate();
		return true;
	}
	
	
	private void moveObject() {
		if(!pathList.isEmpty() && lastTouchCoordinates.distance(pathList.get(0)) < 5) {
			pathList.remove(0);
		}	
		
		if(!pathList.isEmpty()) {
			stickSlipControl.updateActuator(lastTouchCoordinates, pathList.get(0));
		}
	}
	
	
	private void drawPath(MotionEvent event) {
		
		float touchX = event.getX();
		float touchY = event.getY();

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				// remove old path
				drawCanvas.drawColor(Color.WHITE);
				// clear old path
				pathList.clear();
				pathList.add(new Vector2D(touchX, touchY));
				// start drawing
			    drawPath.moveTo(touchX, touchY);
			    break;
			    
			case MotionEvent.ACTION_MOVE:
				pathList.add(new Vector2D(touchX, touchY));
			    drawPath.lineTo(touchX, touchY);
			    break;
			    
			case MotionEvent.ACTION_UP:
			    drawCanvas.drawPath(drawPath, drawPaint);
			    drawPath.reset();
			    break;
			    
			default:
				break;
		}
	}
	
	
	// only for debugging purpose
	private Runnable coordinateUpdater = new Runnable() {		
		@Override
		public void run() {
			
			if(!pathList.isEmpty()) {
				// draw current position
				if(null != lastTouchCoordinates) {
					drawCanvas.drawPoint(lastTouchCoordinates.getX(), lastTouchCoordinates.getY(), whitePaint);
				}
				
				lastTouchCoordinates = lastTouchCoordinates.add(stickSlipControl.getNextPosition());
				drawCanvas.drawPoint(lastTouchCoordinates.getX(), lastTouchCoordinates.getY(), drawPaint);
	
				moveObject();
			}
		}
	};
}
