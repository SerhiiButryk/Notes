#!/bin/bash

# Copyright 2022. Happy coding ! :)
# Author: Serhii Butryk 

# This is simple script to run tests on Android emulator.
# The script will start emulators if it's needed.
# But it doesn't download Emulator images.
# They should be downloaded and available in advance.

# Fail if somthing is wrong
set -e

# Add untility scripts
SCRIPT_RELEVANT_PATH=$( dirname $BASH_SOURCE[0] )
. $SCRIPT_RELEVANT_PATH/utility_functions.sh

# Check if env variable is defined
# if [ -z $ANDROID_SDK_ROOT ]
# then
#     print_error "ANDROID_SDK_ROOT is undefined, aborting..."
#     exit 1
# fi

# # Check if env variable is defined
# if [ -z $ANDROID_CMD_TOOLS ]
# then
#     print_error "ANDROID_CMD_TOOLS is undefined, aborting..."
#     exit 1
# fi

# # Local pathes
# SCRIPT_ABSOLUTE_PATH=$(pwd)
# EMULATOR_DIR="${ANDROID_SDK_ROOT}/emulator"
# TEST_RESULT_DIR="${SCRIPT_RELEVANT_PATH}/../test-results"
# CODE_COVERAGE_FOLDER_NAME="$TEST_RESULT_DIR/codecoverage"

# # Android emulator API level configs
# declare -a EMULATOR_APIS=(
#         "26"
#         "30"
#     )

# echo ""
# print_message "******** Running tests on API ${EMULATOR_APIS[@]} levels *********"
# echo ""

# EMULATOR_LIST=$( $EMULATOR_DIR/emulator -list-avds )

# if [ -z "$EMULATOR_LIST" ]
# then
    
#     print_message "No emulators available, creating emulators"

#     # Create amulators
#     for API in $"${EMULATOR_APIS[@]}"
#     do 
#         print_message "Creating emulator Pixel_API_${API}"

#         DEVICE_NAME="Pixel_API_$API"

#         # It requires cmdline tools Android
#         $ANDROID_CMD_TOOLS/bin/avdmanager create avd -n "$DEVICE_NAME" --device "pixel" -k "system-images;android-${API};google_apis_playstore;x86"
#     done
    
# fi

# # Re-check if emulators available
# EMULATOR_LIST=$( $EMULATOR_DIR/emulator -list-avds )

# # Checking the number of emulators
# declare -i emulator_number
# for EMULATOR in $EMULATOR_LIST
# do
#     emulator_number=emulator_number+1
# done

# if [ ! $emulator_number == "2" ]
# then
#    print_error "To run test you need at least 2 emulators available, aborting..."
#    exit 1 
# fi

# print_message "Available $emulator_number emulators:"
# print_message "$EMULATOR_LIST"

# # Delete test results directory
# rm -rf $TEST_RESULT_DIR

# for EMULATOR in $EMULATOR_LIST
# do
    
#     # Create directory for saving test results
#     mkdir -p $TEST_RESULT_DIR/$EMULATOR/reports

#     print_message "******** Starting $EMULATOR emulator *********"

#     # Start emulator from cold start
#     # Run this command in background
#     $EMULATOR_DIR/emulator -avd $EMULATOR -netdelay none -netspeed full -wipe-data -no-boot-anim -no-cache -logcat-output $TEST_RESULT_DIR/$EMULATOR/adb_logs.txt 2>&1 | tee $TEST_RESULT_DIR/$EMULATOR/Emulator.txt &> /dev/null &

#     print_message "******** Running tests on $EMULATOR emulator *********"

#     print_message "Waiting for device to be online"

#     # Wait while a device is online
#     for i in {1..10}
#     do
#         DEVICE_OFFLINE=$(${ANDROID_SDK_ROOT}/platform-tools/adb devices | grep -w "device" | cut -f 2)

#         if [ -z  "$DEVICE_OFFLINE" ]
#         then
#             echo "Waiting..."    
#         else
#             break
#         fi

#         sleep 10
#     done

    # Run tests
    pushd ${SCRIPT_RELEVANT_PATH}/../Notes/ > /dev/null

    # This will run tests and create coverage report
    # ./gradlew --console plain Project:task -Pandroid.testInstrumentationRunnerArguments.class=fullclassbname
    # ./gradlew --console plain -Pandroid.testInstrumentationRunnerArguments.class=com.serhii.apps.notes.AllTests jacocoTestReport

    ./gradlew --console plain -Pandroid.testInstrumentationRunnerArguments.class=com.serhii.apps.notes.AllTests connectedDebugAndroidTest
    
    popd > /dev/null

    # Copying reports
    # cp -rf ${SCRIPT_RELEVANT_PATH}/../Notes/app/build/reports/androidTests/connected/*  ${SCRIPT_RELEVANT_PATH}/../test-results/$EMULATOR/reports

    # print_message "******** Tests are completed *********"

    # print_message "******** Killing emulator *********"

    # # Stop all running emulators
    # ${ANDROID_SDK_ROOT}/platform-tools/adb devices | grep emulator | cut -f1 | while read line; do ${ANDROID_SDK_ROOT}/platform-tools/adb -s $line emu kill; done;

    # # Wait a little
    # sleep 10

# done

# mkdir -p $CODE_COVERAGE_FOLDER_NAME

# Copy files
# cp -rf ${SCRIPT_RELEVANT_PATH}/../Notes/app/build/reports/coverage/androidTest/debug/* $CODE_COVERAGE_FOLDER_NAME

# Show reports
# for EMULATOR in $EMULATOR_LIST
# do
    # print_message "See reports: file://${SCRIPT_ABSOLUTE_PATH}/test-results/$EMULATOR/reports/index.html"
# done

# Show covarage report
# print_message "See coverage reports: file://${SCRIPT_ABSOLUTE_PATH}/test-results/codecoverage/connected/index.html"

# print_message "******** Finished *********"