#!/bin/bash
export $(grep -v '^#' .env.local | tr -d '\r' | xargs)
echo "Using Spring profiles: $SPRING_PROFILES_ACTIVE"

echo "Starting Brickshop application version: brickshop-$APP_VERSION.jar"
java @jvm-options.txt -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar "target/brickshop-$APP_VERSION.jar"