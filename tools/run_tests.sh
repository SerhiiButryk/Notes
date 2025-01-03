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

./gradlew --console plain -Pandroid.testInstrumentationRunnerArguments.class=com.serhii.apps.notes.AllTests connectedDebugAndroidTest

popd > /dev/null

# Copying reports

mkdir -p $CODE_COVERAGE_FOLDER_NAME
mkdir -p $TEST_RESULT_DIR

cp -rf ${SCRIPT_RELEVANT_PATH}/../Notes/app/build/reports/androidTests/connected/*  $TEST_RESULT_DIR

print_message "******** Tests are completed *********"