package org.uta.tcp.client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public enum ServerCommand {
	Invalid_Command,
	Next,
	Previous,
	Select,
	NextScreen,
	PreviousScreen,
	SelectScreen,
	Ack,
	Nack,
	Error;
	
	
	private static final String COMMAND_START_CHAR = "!";
	private static final String COMMAND_SECTION_SEPERATOR = ":";	
	private static final String COMMAND_END = ";";	
	
	private static final Pattern COMMAND_CODE_PATTERN = Pattern.compile(":[0-9]+;");
	
	
	
	public static ServerCommand serverStringToCommand(String serverString) {
		try {
			String[] commands = serverString.trim().split(COMMAND_SECTION_SEPERATOR + "|" + COMMAND_END, 2);
			
			if(commands.length > 0) {
				String strCommand = commands[0].replace(COMMAND_START_CHAR, "").trim();
				
				return Enum.valueOf(ServerCommand.class, strCommand);
			}
		} catch(Exception e) {
			// invalid command string - return invalid command
		}
		
		return Invalid_Command;
	}	
	
	
	public static String createServerCommand(ServerCommand command, String... body) {
		StringBuilder sb = new StringBuilder();	
		sb.append(COMMAND_START_CHAR + command.toString());
		
		for(String s : body) {
			if(!s.isEmpty()) {
				sb.append(COMMAND_SECTION_SEPERATOR).append(s);
			}
		}
		
		sb.append(COMMAND_END);		
		return sb.toString();
	}
	
	
	public static int parseCommandCode(String serverString) {
		int result = Integer.MIN_VALUE;
		
		Matcher matcher = COMMAND_CODE_PATTERN.matcher(serverString);
		
		if(matcher.find()) {
			try {
				result = Integer.parseInt(matcher.group()
						.replace(COMMAND_SECTION_SEPERATOR, "")
						.replace(COMMAND_END, ""));
				
			} catch(NumberFormatException e) {
				// error parsing the string number to integer
			}
		}
				
		return result;
	}
}
