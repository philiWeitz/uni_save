package org.uta.move.stickslip;




public class StickSlipControl {

	// only for debug purpose
	private Vector2D nextExpectedPosition = new Vector2D();
	
	
	private enum Actuator {
		Top,
		Bottom,
		Left,
		Right		
	}
	
	
	public void updateActuator(Vector2D currentPosition, Vector2D destination) {
		
		Actuator topDown;
		Actuator leftRight;
		
		// Decide which actuator should be used		
		Vector2D deviation = currentPosition.minus(destination);
		
		// set the actuator
		if(deviation.getX() < 0) {
			leftRight = Actuator.Left;
			nextExpectedPosition.setX(1);
		} else {
			leftRight = Actuator.Right;
			nextExpectedPosition.setX(-1);
		}
		
		if(deviation.getY() < 0) {
			topDown = Actuator.Top;
			nextExpectedPosition.setY(1);
		} else {
			topDown = Actuator.Bottom;
			nextExpectedPosition.setY(-1);
		}
		
		
		double alpha = calculateAlpha(currentPosition, destination);
		
		if(alpha > 48) {
			System.out.println("Activate actuator " + topDown);
			nextExpectedPosition.setX(0);
		} else if(alpha < 43) {
			System.out.println("Activate actuator " + leftRight);
			nextExpectedPosition.setY(0);
		} else {
			System.out.println("Activate actuator " + topDown + " and actuator " + leftRight);
		}
	}
	
	
	// only for debug purpose
	public Vector2D getNextPosition() {
		return nextExpectedPosition;
	}	
	
	
	private double calculateAlpha(Vector2D currentPosition, Vector2D destination) {
		
		double a = Math.abs(currentPosition.getY() - destination.getY());		
		double b = Math.abs(currentPosition.getX() - destination.getX());
		double c = Math.sqrt((a*a) + (b*b));
		
		double alpha = Math.toDegrees(Math.asin(a/c));
		
		return alpha;
	}
}
