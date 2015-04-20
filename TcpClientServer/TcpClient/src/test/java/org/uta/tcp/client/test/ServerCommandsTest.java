package org.uta.tcp.client.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.uta.tcp.client.ServerCommand;

public class ServerCommandsTest {

	@Test
	public void serverStringToCommandTest() {
	
		String toServerCommand = "!Next;testcommand";
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
		assertEquals("!Next;test", serverString);
	}
}
