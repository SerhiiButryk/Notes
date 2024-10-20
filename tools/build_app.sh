#!/bin/bash

# Copyright 2022. Happy coding ! :)
# Author: Serhii Butryk 

# Simple script.
# 1) Build Notes App
# 2) Generates reports

# Fail if somthing is wrong
set -e

chmod +x ./build_app.sh

# Add untility scripts
SCRIPT_RELEVANT_PATH=$( dirname $BASH_SOURCE[0] )
. ${SCRIPT_RELEVANT_PATH}/utility_functions.sh

SCRIPT_ABSOLUTE_PATH="$( dirname $( pwd )$(cut -c 2- <<< $0) )"

PROJECT_FOLDER="${SCRIPT_RELEVANT_PATH}/../Notes"
OUTPUT_FOLDER="${SCRIPT_RELEVANT_PATH}/../Notes/app/build/outputs"

ARTIFACT_FOLDER_NAME="Notes-App"
REPORTS_FOLDER_NAME="reports"

BUILD_APK=false
BUILD_BUNDLE=false

while [ "${1:-}" != "" ]; do
  case "$1" in
    --apk)
        BUILD_APK=true
        ;;
    --bundle)
        BUILD_BUNDLE=true
        ;;
  esac
  shift
done

print_message "******** Started building *********"

if [ "$BUILD_APK" == true ]; then

    print_message "******** Building APK *********"

    # Build apk
    pushd ${PROJECT_FOLDER}
    ./gradlew assemble
    popd
fi

if [ "$BUILD_BUNDLE" == true ]; then

    print_message "******** Building Bundle *********"
    
    # Build app bundle
    pushd ${PROJECT_FOLDER}
    ./gradlew bundle
    popd
fi

print_message "******** Finished *********"

print_message "******** Running static analysis *********"

# TODO: Add detect https://github.com/detekt/detekt
# Run static analysis
# About lint: https://developer.android.com/studio/write/lint
pushd ${PROJECT_FOLDER}
./gradlew lint
popd

print_message "******** Finished *********"

print_message "******** Copying files *********"

pushd ${SCRIPT_RELEVANT_PATH}/../
    # Clear if it exists
    rm -rf ${SCRIPT_RELEVANT_PATH}/$ARTIFACT_FOLDER_NAME
    # Create folder for artifacts 
    mkdir -p $ARTIFACT_FOLDER_NAME/${REPORTS_FOLDER_NAME}/lint
popd

# This will contains mappings, apk & bundle files and native symbols
cp -rf -v ${PROJECT_FOLDER}/app/build/outputs/* ${SCRIPT_RELEVANT_PATH}/../${ARTIFACT_FOLDER_NAME}

# Copy reports
cp -rf -v ${PROJECT_FOLDER}/app/build/reports/* ${SCRIPT_RELEVANT_PATH}/../${ARTIFACT_FOLDER_NAME}/${REPORTS_FOLDER_NAME}/lint

# Remove some uneeded files
rm -rf -v ${SCRIPT_RELEVANT_PATH}/../${ARTIFACT_FOLDER_NAME}/sdk-dependencies
rm -rf -v ${SCRIPT_RELEVANT_PATH}/../${ARTIFACT_FOLDER_NAME}/logs

# Make an archive
zip -r ${SCRIPT_RELEVANT_PATH}/../App.zip ${SCRIPT_RELEVANT_PATH}/../Notes-App
rm -rf -v ${SCRIPT_RELEVANT_PATH}/../Notes-App

print_message "******** Finished *********"