
SET JAR_FILE=.\tcpLeapMotion.jar

SET keyboardLibDir=.\

SET leapMotionLibDir=.\libs\x64

:: bypass local leap motion directory if LEAP_MOTION_LIBS is defined
IF NOT "%LEAP_MOTION_LIBS%"=="" (
  SET leapMotionLibDir="%LEAP_MOTION_LIBS%"
)

java -Djava.library.path=%keyboardLibDir%;%leapMotionLibDir% -jar %JAR_FILE%  

pause