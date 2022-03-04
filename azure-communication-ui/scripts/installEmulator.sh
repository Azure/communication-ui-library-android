#!/usr/bin/env bash

#export ANDROID_SDK_ROOT=$PWD/android-sdk
SYS_IMG_TAG=google_apis
CPU=x86_64
SYSTEM_IMAGE="system-images;android-29;${SYS_IMG_TAG};${CPU}"

# Install AVD files
echo "y" | $ANDROID_HOME/tools/bin/sdkmanager --sdk_root=${ANDROID_HOME} --install ${SYSTEM_IMAGE}

# Create emulator
echo "no" | $ANDROID_HOME/tools/bin/avdmanager create avd -n xamarin_android_emulator -k ${SYSTEM_IMAGE} --force --device "pixel_xl" --tag "${SYS_IMG_TAG}"

echo "avdmanager successfully created $($ANDROID_HOME/emulator/emulator -list-avds)"
echo "Starting emulator"

sed -i'' -e "s%android-sdk/system-images/android-29/${SYS_IMG_TAG}/${CPU}/%system-images/android-29/${SYS_IMG_TAG}/${CPU}/%g" ~/.android/avd/xamarin_android_emulator.avd/config.ini
sed -i'' -e "s%hw.keyboard=no%hw.keyboard=yes%g" ~/.android/avd/xamarin_android_emulator.avd/config.ini


#$PWD/android-sdk/platform-tools/adb kill-server

# Start emulator in background 
$ANDROID_HOME/emulator/emulator @xamarin_android_emulator -no-snapshot -skin 1130x2360 -accel auto -gpu auto -screen touch -camera-back emulated -camera-front emulated -use-host-vulkan -dns-server 8.8.8.8  &
$ANDROID_HOME/platform-tools/adb wait-for-device shell 'while [[ -z $(getprop sys.boot_completed | tr -d '\r') ]]; do sleep 1; done; input keyevent 82'

$ANDROID_HOME/platform-tools/adb devices


$ANDROID_HOME/platform-tools/adb shell 'echo "chrome --disable-fre --no-default-browser-check --no-first-run" > /data/local/tmp/chrome-command-line'
$ANDROID_HOME/platform-tools/adb shell svc power stayon true
$ANDROID_HOME/platform-tools/adb shell settings put system screen_off_timeout 600000
$ANDROID_HOME/platform-tools/adb shell am broadcast -a android.intent.action.CLOSE_SYSTEM_DIALOGS
$ANDROID_HOME/platform-tools/adb shell input keyevent 66
# press home key
$ANDROID_HOME/platform-tools/adb shell input keyevent 3
echo "Emulator started"
