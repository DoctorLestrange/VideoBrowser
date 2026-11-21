@echo off
setlocal
cd /d "%~dp0"

set JAR_PATH=target\VideoBrowser-0.1.jar
if not exist "%JAR_PATH%" set JAR_PATH=VideoBrowser-0.1.jar

if not exist "%JAR_PATH%" (
    echo [ERROR] Application JAR file not found.
    echo Please run 'mvn clean package -DskipTests' first.
    pause
    exit /b 1
)

start "" javaw -jar "%JAR_PATH%" %*
exit /b 0
