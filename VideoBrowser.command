#!/usr/bin/env bash
DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$DIR"

JAR_PATH="target/VideoBrowser-0.1.jar"
if [ ! -f "$JAR_PATH" ]; then
    JAR_PATH="VideoBrowser-0.1.jar"
fi

if [ ! -f "$JAR_PATH" ]; then
    echo "[ERROR] Application package not found."
    echo "Please build the project first: mvn clean package -DskipTests"
    read -p "Press enter to exit..."
    exit 1
fi

exec java -jar "$JAR_PATH" "$@"
