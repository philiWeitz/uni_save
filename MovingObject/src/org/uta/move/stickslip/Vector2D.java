package org.uta.move.stickslip;

public class Vector2D {

	private float x;
	private float y;
	
	
	public Vector2D() {
		this.x = 0;
		this.y = 0;
	}
	
	
	public Vector2D(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	
	public float getX() {
		return x;
	}
	
	
	public void setX(float x) {
		this.x = x;
	}
	
	
	public float getY() {
		return y;
	}
	
	
	public void setY(float y) {
		this.y = y;
	}
	
	
	public Vector2D add(Vector2D vector) {
		
		Vector2D result = new Vector2D(
				x + vector.getX(),
				y + vector.getY());
		
		return result;
	}
	
	
	public Vector2D minus(Vector2D vector) {
		
		Vector2D result = new Vector2D(
				x - vector.getX(),
				y - vector.getY());
		
		return result;
	}
	
	
	public double distance(Vector2D vector) {
		double a = Math.abs(x - vector.getX());		
		double b = Math.abs(y - vector.getY());
		
		return Math.sqrt((a*a) + (b*b));
	}	
}
