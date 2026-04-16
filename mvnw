#!/bin/sh
# Maven Wrapper script for Unix/macOS
# Downloads Maven automatically if not present

set -e

MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR:-$(cd "$(dirname "$0")" && pwd)}"
MAVEN_WRAPPER_PROPERTIES_PATH="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties"
MAVEN_WRAPPER_JAR_PATH="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"

# Read distributionUrl from properties
if [ -f "$MAVEN_WRAPPER_PROPERTIES_PATH" ]; then
    distributionUrl=$(grep 'distributionUrl' "$MAVEN_WRAPPER_PROPERTIES_PATH" | cut -d'=' -f2-)
fi

MAVEN_HOME="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven"
MAVEN_CMD="$MAVEN_HOME/bin/mvn"

if [ ! -f "$MAVEN_CMD" ]; then
    echo "Downloading Maven..."
    mkdir -p "$MAVEN_HOME"
    
    if command -v curl > /dev/null 2>&1; then
        curl -fsSL "$distributionUrl" -o "$MAVEN_HOME/maven.zip"
    elif command -v wget > /dev/null 2>&1; then
        wget -q "$distributionUrl" -O "$MAVEN_HOME/maven.zip"
    else
        echo "Error: curl or wget required"
        exit 1
    fi
    
    unzip -q "$MAVEN_HOME/maven.zip" -d "$MAVEN_HOME"
    mv "$MAVEN_HOME"/apache-maven-*/* "$MAVEN_HOME/"
    rm "$MAVEN_HOME/maven.zip"
    chmod +x "$MAVEN_CMD"
fi

exec "$MAVEN_CMD" "$@"
