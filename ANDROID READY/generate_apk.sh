#!/bin/bash
if [ ! -f "./gradlew" ]; then
    echo "gradlew not found. Please open the project in Android Studio first to generate wrapper, or run 'gradle wrapper'."
    # Fallback if gradle is in path
    if command -v gradle &> /dev/null; then
        gradle assembleRelease
    else
        echo "Error: Gradle not found."
    fi
else
    ./gradlew assembleRelease
fi
