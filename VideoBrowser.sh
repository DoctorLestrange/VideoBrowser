#!/usr/bin/env bash
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

JAR_PATH="target/VideoBrowser-0.1.jar"
if [ ! -f "$JAR_PATH" ]; then
    JAR_PATH="VideoBrowser-0.1.jar"
fi

if [ ! -f "$JAR_PATH" ]; then
    echo "[ERROR] Application package not found."
    echo "Please build the project first: mvn clean package -DskipTests"
    exit 1
fi

exec java -jar "$JAR_PATH" "$@"
