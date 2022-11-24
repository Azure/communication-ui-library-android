#!/usr/bin/env bash
# usage:  ./installAab.sh {release|debug}
cd ..
set -e
./gradlew clean :demo-app:bundleCalling$1

DEVICE=($(adb devices | grep "device$" | sed -e "s|device||g"))
OUTPUT_DIR=./demo-app/build/outputs
BUNDLE=$OUTPUT_DIR/bundle/calling$1/demo-app-calling-$1.aab
APK_SET=$OUTPUT_DIR/app.apks
# obtained from https://github.com/google/bundletool/releases for convenience, may not be latest
BUNDLETOOL=./scripts/bundletool.jar
java -jar ${BUNDLETOOL} build-apks --connected-device --device-id=${DEVICE} --bundle=$BUNDLE --output=$APK_SET
java -jar ${BUNDLETOOL} install-apks --device-id=${DEVICE} --apks=$APK_SET
rm $OUTPUT_DIR/*.apks
