package org.uta.move.view;

import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

public class DrawView extends View {
		
	private boolean drawingActive = false;
	private boolean motionActive = false;	
	
	private Pair<Float, Float> lastTouchCoordinates;
	
	private Path drawPath;
	private Paint drawPaint;
	private Paint canvasPaint;
	private Canvas drawCanvas;
	private Bitmap canvasBitmap;
	
	private Paint whitePaint;
	
	private List<Pair<Float, Float>> pathList = new LinkedList<Pair<Float,Float>>();
	
	
	public DrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init();
	}
	
	
	public List<Pair<Float, Float>> getPath() {
		return pathList;
	}
	
	
	public void setDrawingActive(boolean active) {
		motionActive = false;
		drawingActive = active;
	}
	
	
	public void setMotionActive(boolean active) {
		drawingActive = false;
		motionActive = active;
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
		whitePaint.setStrokeWidth(6);
	
		canvasPaint = new Paint(Paint.DITHER_FLAG);	
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
		
		if(null != lastTouchCoordinates) {
			drawCanvas.drawPoint(lastTouchCoordinates.first, lastTouchCoordinates.second, whitePaint);
		}
		
		lastTouchCoordinates = 
				new Pair<Float, Float>(event.getX(), event.getY());
		
		drawCanvas.drawPoint(event.getX(), event.getY(), drawPaint);
		
		if(drawingActive) {
			drawPath(event);
		} else if(motionActive) {
			
		}
		
		invalidate();
		return true;
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
				// start drawing
			    drawPath.moveTo(touchX, touchY);
			    break;
			    
			case MotionEvent.ACTION_MOVE:
				pathList.add(new Pair<Float, Float>(touchX, touchY));
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
}
