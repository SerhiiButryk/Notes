#!/bin/bash

# Copyright 2022. Happy coding ! :)
# Author: Serhii Butryk 

# This is simple script to run tests on Android emulator.
# The script will start emulators if it's needed.
# But it doesn't download Emulator images.
# They should be downloaded and available in advance.

# Fail if somthing is wrong
set -e

# Tests are not running on Jenkins, because emulator fails to launch on Jenkins.
if [ "$JENKINS_CONTEXT" = true ]
then
    echo "Skipping tests on Jenkins"
    exit 0
fi

# Add untility scripts
SCRIPT_RELEVANT_PATH=$( dirname $BASH_SOURCE[0] )
. $SCRIPT_RELEVANT_PATH/utility_functions.sh

# Check if env variable is defined
if [ -z $ANDROID_SDK_ROOT ]
then
    echo "ANDROID_SDK_ROOT is undefined, aborting..."
    exit 1
fi

# Check if env variable is defined
if [ -z $ANDROID_CMD_TOOLS ]
then
    echo "ANDROID_CMD_TOOLS is undefined, aborting..."
    exit 1
fi

# Local pathes
EMULATOR_DIR="${ANDROID_SDK_ROOT}/emulator"
TEST_RESULT_DIR="${SCRIPT_RELEVANT_PATH}/../test-results"

# Android emulator API level configs
declare -a EMULATOR_APIS=(
        "26"
        "30"
    )

echo "******** Running tests on API ${EMULATOR_APIS[@]} levels *********"
echo ""

EMULATOR_LIST=$( $EMULATOR_DIR/emulator -list-avds )

if [ -z "$EMULATOR_LIST" ]
then
    
    echo "No emulators available, creating emulators"
    echo ""

    # Create amulators
    for API in $"${EMULATOR_APIS[@]}"
    do 
        echo "Creating emulator Pixel_API_${API}"
        echo ""

        DEVICE_NAME="Pixel_API_$API"

        # It requires cmdline tools Android
        $ANDROID_CMD_TOOLS/bin/avdmanager create avd -n "$DEVICE_NAME" --device "pixel" -k "system-images;android-${API};google_apis_playstore;x86"
    done
    
fi

# Re-check if emulators available
EMULATOR_LIST=$( $EMULATOR_DIR/emulator -list-avds )

# Checking the number of emulators
declare -i emulator_number
for EMULATOR in $EMULATOR_LIST
do
    emulator_number=emulator_number+1
done

if [ ! $emulator_number == "2" ]
then
   echo "To run test you need at least 2 emulators available, aborting..."
   exit 1 
fi

echo "Available $emulator_number emulators:"
echo $EMULATOR_LIST
echo ""

# Delete test results directory
rm -rf $TEST_RESULT_DIR

for EMULATOR in $EMULATOR_LIST
do
    
    # Create directory for saving test results
    mkdir -p $TEST_RESULT_DIR/$EMULATOR/reports

    echo "******** Starting $EMULATOR emulator *********"
    echo ""

    # Start emulator from cold start
    # Run this command in background
    $EMULATOR_DIR/emulator -avd $EMULATOR -netdelay none -netspeed full -wipe-data -no-boot-anim -no-cache -logcat-output $TEST_RESULT_DIR/$EMULATOR/adb_logs.txt 2>&1 | tee $TEST_RESULT_DIR/$EMULATOR/Emulator.txt &> /dev/null &

    echo "******** Running tests on $EMULATOR emulator *********"
    echo ""

    # Wait 60 seconds for device to be online
    sleep 60

    # Run tests
    pushd ${SCRIPT_RELEVANT_PATH}/../Notes/ > /dev/null
    
    ./gradlew connectedAndroidTest
    
    popd > /dev/null

    # Copying reports
    cp -rf ${SCRIPT_RELEVANT_PATH}/../Notes/app/build/reports/androidTests/connected/*  ${SCRIPT_RELEVANT_PATH}/../$TEST_RESULT_DIR/$EMULATOR/reports

    echo ""
    echo "******** Tests are completed *********"
    echo ""

    echo "******** Killing emulator *********"
    echo ""

    # Stop all running emulators
    ${ANDROID_SDK_ROOT}/platform-tools/adb devices | grep emulator | cut -f1 | while read line; do ${ANDROID_SDK_ROOT}/platform-tools/adb -s $line emu kill; done;

done

echo ""
echo "******** Finished *********"
echo ""