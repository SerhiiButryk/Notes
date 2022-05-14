#!/bin/bash

# Copyright 2022. Happy coding ! :)
# Author: Serhii Butryk 

# This is siple stript to run tests on Android emulator.
# Before running tests 2 Android Emulators should be available.
# One for testing on mis supported SDK version and another one 
# for max supported SDK version.

# Fail if somthing is wrong
set -e

# Add untility scripts
SCRIPT_RELEVANT_PATH=$( dirname $BASH_SOURCE[0] )
. $SCRIPT_RELEVANT_PATH/utility_functions.sh

# Check if env variable is defined
if [[ -z $ANDROID_SDK_ROOT ]]
then
    echo "ANDROID_SDK_ROOT is undefined, aborting..."
    exit 1
fi

# Local pathes
EMULATOR_DIR="${ANDROID_SDK_ROOT}/emulator"
TEST_RESULT_DIR="${SCRIPT_RELEVANT_PATH}/../test-results"

echo "******** Running tests *********"
echo ""

EMULATOR_LIST=$( $EMULATOR_DIR/emulator -list-avds )
if [[ -z $EMULATOR_LIST ]]
then
    # Local emulators can be invisible on Jenkins
    # We need to create new explicitly. 
    echo "No emulators available, creating emulators"
    echo ""

    echo "Creating emulator Pixel_API_26"
    echo ""
    
    # It requires latest cmdline tools Android
    ${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin/avdmanager create avd -n test -k "system-images;android-25;google_apis;x86"
    
    # ${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin/avdmanager create avd -n Pixel_API_26 --device "pixel" -k "system-images;android-26;google_apis_playstore;x86"
    
    echo "Creating emulator Pixel_API_30"
    echo ""

    # It requires latest cmdline tools Android
    # ${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin/avdmanager create avd -n Pixel_API_30 --device "pixel" -k "system-images;android-30;google_apis_playstore;x86"

fi

# Re-check if emulators available
EMULATOR_LIST=$( $EMULATOR_DIR/emulator -list-avds )

# Checking the number of emulators
declare -i emulator_number
for EMULATOR in $EMULATOR_LIST
do
    emulator_number=emulator_number+1
done

if [[ ! $emulator_number == "2" ]] 
then
   echo "To run test you need at least 2 emulators available, aborting..."
   exit 1 
fi

echo "Available $emulator_number emulators:"
echo ""
echo $EMULATOR_LIST
echo ""

# Delete test results directory
rm -rf $TEST_RESULT_DIR

for EMULATOR in $EMULATOR_LIST
do
    
    # Create directory for saving test results
    mkdir -p $TEST_RESULT_DIR/$EMULATOR

    echo "******** Starting $EMULATOR emulator *********"
    echo ""

    # Start emulator from cold start
    # Run this command in background
    $EMULATOR_DIR/emulator -avd $EMULATOR -netdelay none -netspeed full -wipe-data -no-boot-anim -no-cache -logcat-output $TEST_RESULT_DIR/$EMULATOR/adb_logs.txt 2>&1 | tee $TEST_RESULT_DIR/$EMULATOR/Emulator.txt &> /dev/null &

    echo "******** Running tests on $EMULATOR emulator *********"
    echo ""

    # Wait 30 seconds for device to be online
    sleep 30

    # Run tests
    pushd ${SCRIPT_RELEVANT_PATH}/../Notes/ > /dev/null
    
    ./gradlew connectedAndroidTest
    
    popd > /dev/null

    echo ""
    echo "******** Tests are completed *********"
    echo ""

    echo "******** Killing emulator *********"
    echo ""

    # Stop all running emulators
    ${ANDROID_SDK_ROOT}/platform-tools/adb devices | grep emulator | cut -f1 | while read line; do adb -s $line emu kill; done

done

echo ""
echo "******** Finished *********"
echo ""