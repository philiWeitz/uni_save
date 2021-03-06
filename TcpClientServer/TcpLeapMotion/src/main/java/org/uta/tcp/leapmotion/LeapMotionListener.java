package org.uta.tcp.leapmotion;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.uta.tcp.client.SerialPortController;
import org.uta.tcp.client.ServerCommand;
import org.uta.tcp.client.TcpClient;
import org.uta.tcp.client.TcpUtil;

import com.leapmotion.leap.CircleGesture;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.Gesture.State;
import com.leapmotion.leap.Gesture.Type;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.SwipeGesture;
import com.leapmotion.leap.Vector;

public class LeapMotionListener extends Listener {

	private static Logger LOG = LogManager.getLogger(LeapMotionListener.class);	

	private static final long GESTURE_TIME_OUT = 300;

	private long lastGestureTime = 0;

	
	public void onConnect(Controller controller) {
		LOG.info("Connected to leap motion");

		//Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		
		controller.enableGesture(Type.TYPE_SWIPE);
		controller.enableGesture(Type.TYPE_CIRCLE);
		controller.enableGesture(Type.TYPE_SCREEN_TAP);
				
		controller.setPolicy(Controller.PolicyFlag.POLICY_BACKGROUND_FRAMES);
		controller.setPolicy(Controller.PolicyFlag.POLICY_IMAGES);
		controller.setPolicy(Controller.PolicyFlag.POLICY_OPTIMIZE_HMD);

		//controller.config().setFloat("Gesture.Circle.MinRadius", 30f);
		//controller.config().setFloat("Gesture.Circle.MinArc", (float) (Math.PI * 1.3));
		
		controller.config().setFloat("Gesture.Swipe.MinLength", 150f);
		controller.config().setFloat("Gesture.Swipe.MinVelocity", 1000f);
		
		controller.config().save();
	}
	

	public void onFrame(Controller controller) {

		Frame frame = controller.frame();

		// gesture timeout
		if (new Date().getTime() > lastGestureTime) {
			
			for (Gesture g : frame.gestures()) {

				// screen tap gesture only states STATE_STOP
				if (g.state() == State.STATE_START 
						|| g.type() == Type.TYPE_SCREEN_TAP && g.state() == State.STATE_STOP) {
					
					LOG.debug(g.type() + " gesture detected");
					
					switch (g.type()) {
						case TYPE_SWIPE:
							swipeGesture(g);
							break;
						case TYPE_CIRCLE:
							circularGesture(g);
							break;
						case TYPE_SCREEN_TAP:
							screenTapGesture(g);
							break;							
						default:
							break;
					}

					// adds the gesture timeout after each recognized gesture
					lastGestureTime = new Date().getTime() + GESTURE_TIME_OUT;

					break;				
				}
			}
		}
	}

	
	private void circularGesture(Gesture g) {
		CircleGesture circle = new CircleGesture(g);
		
		if (circle.pointable().direction().angleTo(circle.normal()) <= Math.PI / 4) {

			// Gesture: Focus Right
			SerialPortController.getPortInstance(TcpUtil.dtrPort).setDtrPulse();
			TcpClient.getInstance().sendCommand(ServerCommand.Next);

		} else {
			
			// Gesture: Focus Left
			SerialPortController.getPortInstance(TcpUtil.dtrPort).setDtrPulse();
			TcpClient.getInstance().sendCommand(ServerCommand.Previous);
		}
	}
	
	
	private void swipeGesture(Gesture g) {
		SwipeGesture  swipe = new SwipeGesture(g);
		Vector direction = swipe.direction();
		float directionX = direction.getX();
			
		// Right to Left Swipe Gestures
		if (directionX < 0) {
			SerialPortController.getPortInstance(TcpUtil.dtrPort).setDtrPulse();
			TcpClient.getInstance().sendCommand(ServerCommand.NextScreen);
		}
		
		// Left to Right Swipe Gestures
		else if (directionX > 0) {
			SerialPortController.getPortInstance(TcpUtil.dtrPort).setDtrPulse();
			TcpClient.getInstance().sendCommand(ServerCommand.PreviousScreen);
		}
		
	}
	
	
	private void screenTapGesture(Gesture g) {
		SerialPortController.getPortInstance(TcpUtil.dtrPort).setDtrPulse();
		TcpClient.getInstance().sendCommand(ServerCommand.Select);
	}	
}
