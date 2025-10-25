@echo off
REM Simple build script for Windows
REM Make sure lib\junit-4.13.2.jar and org.json are available in the lib folder.

echo Creating build directory...
if not exist source mkdir source

echo Compiling Java source files...
javac -d source -cp "lib/*" source\**\*.java

if %errorlevel% neq 0 (
    echo Compilation failed.
    pause
    exit /b %errorlevel%
)

echo Compilation complete.
echo To run manually: java -cp "source;lib/*" mastersofmq.MastersOfMQ

echo Running program...
java -cp "source;lib/*" mastersofmq.MastersOfMQ

pause