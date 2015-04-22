package org.uta.tcp.client.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.uta.tcp.client.ServerCommand;

public class ServerCommandsTest {

	@Test
	public void serverStringToCommandTest() {
	
		String toServerCommand = "!Next:testcommand;";
		ServerCommand command = ServerCommand.serverStringToCommand(toServerCommand);
		assertEquals(ServerCommand.Next, command);

		toServerCommand = "!Next;";
		command = ServerCommand.serverStringToCommand(toServerCommand);
		assertEquals(ServerCommand.Next, command);
	}
	
	
	@Test
	public void commandToServerStringTest() { 
		
		ServerCommand command = ServerCommand.Next;
		String body = "test";
		
		String serverString = ServerCommand.createServerCommand(command, body);
		assertEquals("!Next:test;", serverString);
	}
	
	
	@Test
	public void parseCommandCodeTest() { 
		
		String toServerCommand = "!Ack:!Next:2;";	
		int commandCode = ServerCommand.parseCommandCode(toServerCommand);
		assertEquals(2, commandCode);
		
		toServerCommand = "!Error:!Next:503;";
		commandCode = ServerCommand.parseCommandCode(toServerCommand);
		assertEquals(503, commandCode);
		
		toServerCommand = "!Error:!Next:-3;";
		commandCode = ServerCommand.parseCommandCode(toServerCommand);
		assertEquals(-3, commandCode);
		
		toServerCommand = "!Next:testcommand;";
		commandCode = ServerCommand.parseCommandCode(toServerCommand);
		assertEquals(Integer.MIN_VALUE, commandCode);			
	}
}
