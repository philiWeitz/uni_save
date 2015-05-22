package org.uta.tcp.jni;

public class KeyboardUtil {
	
	static {		
		if(is64BitArchitecture()) {
			System.loadLibrary("libs/keyboardAccess_x64");	
		} else {
			System.loadLibrary("libs/keyboardAccess_x86");
		}
	}

	
	private KeyboardUtil() {
		
	}
	
	
	private static boolean is64BitArchitecture() {
		boolean is64bit = false;
		
		if (System.getProperty("os.name").contains("Windows")) {
		    is64bit = (System.getenv("ProgramFiles(x86)") != null);
		} else {
		    is64bit = (System.getProperty("os.arch").indexOf("64") != -1);
		}
		
		return is64bit;
	}
	

	public static native short[] getKeyboardState();
}
