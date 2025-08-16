#!/bin/bash

APP_NAME="SigmaBotTeams"
MAIN_JAR="sigma-bot-teams-1.0-SNAPSHOT.jar"
MAIN_CLASS="com.htth.sigmabotteams.MainKt"
OUTPUT_DIR="installer"

# Optional: Icon for the app (PNG for Linux/macOS, ICO for Windows)
# ICON="icon.png"

# Build JVM Fat Jar
./gradlew clean shadowJar

# Create the installer
jpackage \
  --input build/libs \
  --name "$APP_NAME" \
  --main-jar "$MAIN_JAR" \
  --main-class "$MAIN_CLASS" \
  --type app-image \
  # --icon "$ICON" \
  --dest "$OUTPUT_DIR"

# Move .env file
cp .env "$APP_NAME/bin/.env"