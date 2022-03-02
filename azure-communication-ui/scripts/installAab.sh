#!/usr/bin/env bash
cd ..
set -e
./gradlew clean :azure-communication-ui-demo-app:bundleDebug

OUTPUT_DIR=./azure-communication-ui-demo-app/build/outputs
# obtained from https://github.com/google/bundletool/releases for convenience, may not be latest
BUNDLETOOL=./scripts/bundletool.jar
java -jar ${BUNDLETOOL} build-apks --connected-device --device-id=$1 --bundle=$OUTPUT_DIR/bundle/debug/azure-communication-ui-demo-app-debug.aab --output=$OUTPUT_DIR/app_debug.apks
java -jar ${BUNDLETOOL} install-apks --device-id=$1 --apks=$OUTPUT_DIR/app_debug.apks
rm $OUTPUT_DIR/*.apks