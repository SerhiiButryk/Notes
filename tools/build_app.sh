#!/bin/bash

# Copyright 2022. Happy coding ! :)
# Author: Serhii Butryk 

# Simple script for building Notes App.

# Fail if somthing is wrong
set -e

# Add untility scripts
SCRIPT_RELEVANT_PATH=$( dirname $BASH_SOURCE[0] )
. ${SCRIPT_RELEVANT_PATH}/utility_functions.sh

SCRIPT_ABSOLUTE_PATH="$( dirname $( pwd )$(cut -c 2- <<< $0) )"

BUILD_FOLDER="${SCRIPT_ABSOLUTE_PATH}/../Notes/"
APK_FILES_FOLDER="${SCRIPT_ABSOLUTE_PATH}/../Notes/app/build/outputs/apk"
MAPPING_FOLDER="${SCRIPT_ABSOLUTE_PATH}/../Notes/app/build/outputs/mapping"
ARTIFACT_FOLDER_NAME="dist"

print_message "******** Started building *********"

# Build app
pushd ${BUILD_FOLDER}
./gradlew clean assemble
popd

print_message "******** Finished *********"

print_message "******** Prepare artifacts *********"

pushd ${SCRIPT_ABSOLUTE_PATH}/../
# Create folder for artifacts 
mkdir $ARTIFACT_FOLDER_NAME
popd 

cp -rf -v ${APK_FILES_FOLDER}/* ${SCRIPT_ABSOLUTE_PATH}/../${ARTIFACT_FOLDER_NAME}
cp -rf -v ${MAPPING_FOLDER}/* ${SCRIPT_ABSOLUTE_PATH}/../${ARTIFACT_FOLDER_NAME}

print_message "******** Finished *********"