package org.uta.tcp.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.uta.tcp.server.ServerUtil;
import org.uta.tcp.server.TCPObject;

import com.google.gson.Gson;

public class TCPClient {
	private static Logger LOG = LogManager.getLogger(TCPClient.class);

	private Socket clientSocket;
	private OutputStream outToServer;

	
	public void sendDataToServer(String tag) {
		TCPObject toSend = new TCPObject();
		toSend.setTime(System.nanoTime());
		toSend.setTag(tag);
		
		new Thread(new SendDataThread(toSend)).run();
	}
	
	
	private boolean connectToServer() {
		try {
			clientSocket = new Socket(ServerUtil.SERVER_ADDRESS,
					ServerUtil.TCP_PORT);
			outToServer = clientSocket.getOutputStream();

			return true;
		} catch (UnknownHostException e) {
			LOG.error("Can't connect to server " + ServerUtil.SERVER_ADDRESS, e);
		} catch (IOException e) {
			LOG.error("Error: can't connect to server \"" + ServerUtil.SERVER_ADDRESS + "\"");
		}
		return false;
	}

	
	private void disconnectFromServer() {
		if (null != clientSocket) {
			try {
				outToServer.close();
				clientSocket.close();
			} catch (IOException e) {
				LOG.error("Error while disconnecting from server", e);
			}
		}
	}

	
	private class SendDataThread implements Runnable {
		private TCPObject data;
		
		public SendDataThread(TCPObject data) {
			this.data = data;
		}
		
		public void run() {
			if(connectToServer()) {

				try {
					outToServer.write(new Gson().toJson(data).getBytes());
				} catch (IOException e) {
					LOG.error("Error while sending data to server", e);
				}
				
				disconnectFromServer();		
			}
		}
	}
}
