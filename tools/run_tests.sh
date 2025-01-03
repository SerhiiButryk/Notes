#!/bin/bash

# Copyright 2025. Happy coding ! :)
# Author: Serhii Butryk 

# This is a simple script to run Android tests

# Fail if somthing is wrong
set -e

# Add untility scripts
SCRIPT_RELEVANT_PATH=$( dirname $BASH_SOURCE[0] )
. $SCRIPT_RELEVANT_PATH/utility_functions.sh

# Local pathes
SCRIPT_ABSOLUTE_PATH=$(pwd)
EMULATOR_DIR="${ANDROID_SDK_ROOT}/emulator"
TEST_RESULT_DIR="${SCRIPT_RELEVANT_PATH}/../Notes-App/test-results"
CODE_COVERAGE_FOLDER_NAME="${SCRIPT_RELEVANT_PATH}/../Notes-App/test-coverage"

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