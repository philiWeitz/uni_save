package org.uta.tcp.client;


public enum ServerCommand {
	Invalid_Command,
	
	Rts,
	Dtr,
	Hc,
	
	Next,
	Previous,
	Select,
	NextScreen,
	PreviousScreen,
	SelectScreen,
	SetIcon,
	SetGroup,
	SetAnimation,
	SetLamp,
	SetDigits,
	RequestInfo,
	SetScrBright,
	CurrentState,
	UnlightAll;
	
	
	private static final String COMMAND_START_CHAR = "!";
	private static final String COMMAND_END_CHAR = ";";	
	
	
	public static ServerCommand serverStringToCommand(String serverString) {
		try {
			String[] commands = serverString.trim().split(COMMAND_END_CHAR, 2);
			
			if(commands.length > 0) {
				String strCommand = commands[0].replace(COMMAND_START_CHAR, "").trim();
				return Enum.valueOf(ServerCommand.class, strCommand);
			}
		} catch(Exception e) {
			// invalid command string - return invalid command
		}
		
		return Invalid_Command;
	}	
	
	
	public static String createServerCommand(ServerCommand command, String body) {
		return COMMAND_START_CHAR + command.toString() + COMMAND_END_CHAR + body;		
	}	
}
