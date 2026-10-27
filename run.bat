@echo off
cd /d "%~dp0"
echo Compiling Hostel Management System...
javac src\HostelServer.java
if errorlevel 1 (
  echo.
  echo Compilation failed. Please install JDK 11 or newer and check PATH.
  pause
  exit /b 1
)
echo Starting server...
java -cp src HostelServer
pause
