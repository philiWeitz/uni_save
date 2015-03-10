package org.uta.tcp.leapmotion;

import java.io.IOException;

import org.uta.tcp.client.TcpClient;

import com.leapmotion.leap.Controller;

public class LeapMotionMain {

	public static void main(String[] args) {

		TcpClient.getInstance().connectToServer();
		
		Controller controller = new Controller();
		useListener(controller);
		
		TcpClient.getInstance().disconnectFromServer();
	}
	
	
	
	private static void useListener(Controller controller) {

        LeapMotionListener listener = new LeapMotionListener();
		controller.addListener(listener);

        System.out.println("Press Enter to quit...");
        
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        controller.removeListener(listener);
	}
}
