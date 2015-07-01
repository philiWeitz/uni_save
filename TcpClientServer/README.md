

TCP Client and Server
=================

## Tools which need to be installed
- eclipse (Luna) - https://eclipse.org/downloads/
- required eclipse plugins:
  - Maven Integration for Eclipse

## Compile and run the project
1. Import the project to eclipse
  - select the import as "Existing Maven Projects" and navigate to the TcpClientServer root directory 
  - press the "Finish" button

2. Set the leap motion native path
  - https://developer.leapmotion.com/documentation/java/devguide/Project_Setup.html

3. Set the native library path for keyboard input
  - right click on the LeapMotionProject -> Properties
  - select "Java Build Path" -> Source -> TcpLeapMotion/src/main/java
  - select the "Native library location", press "Edit" and select the TcpLeapMotion root folder
  
4. Build the project for external use (maven build)
  - open the pom.xml file in TcpClientServer
  - change the \<leap.motion.path\> and \<leap.motion.version\> according to your local installation
  - right click on TcpLeapMotion project and select "Run as" -> "Maven Build"
  - type in "clean install" into the goal field and press "Run"
  - navigate to "TcpLeapMotion -> target" and select the jar-with-dependencies

5. Run the TcpLeapMotion project inside eclipse
  - right click on the TcpLeapMotion project -> "Run as" -> "Java Application"
  - select "LeapMotionMain" as the entrance file

## Running the TcpLeapMotion without eclipse
- specify the library path to the leap motion native library and the keyboard library by using "-Djava.library.path=\<leapMotionPath\>;\<TcpLeapMotionRootDirectory\>"
- http://examples.javacodegeeks.com/java-basics/java-library-path-what-is-it-and-how-to-use
- when using the RunTcpLeapMotion.bat, a "LEAP_MOTION_LIBS" environment variable can be defined to use the local leap motion installation
  
