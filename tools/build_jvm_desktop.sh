#!/bin/bash

# Copyright 2026. Happy coding ! :)
# Author: Serhii Butryk 

# Fail if somthing is wrong
set -e

# Add untility scripts
SCRIPT_RELEVANT_PATH=$( dirname $BASH_SOURCE[0] )
. ${SCRIPT_RELEVANT_PATH}/utility_functions.sh

SCRIPT_ABSOLUTE_PATH="$( dirname $( pwd )$(cut -c 2- <<< $0) )"

PROJECT_FOLDER="${SCRIPT_RELEVANT_PATH}/../multiplatform/Notes"
OUTPUT_FOLDER="${SCRIPT_RELEVANT_PATH}/../multiplatform/Notes/composeApp/build/outputs"

ARTIFACT_FOLDER_NAME="Notes-App"
REPORTS_FOLDER_NAME="reports"

print_message "******** Started building *********"

# Build app
pushd ${PROJECT_FOLDER}
./gradlew packageUberJarForCurrentOS --console=plain
popd

print_message "******** Finished *********"

print_message "******** Copying files *********"

pushd ${SCRIPT_RELEVANT_PATH}/../
    # Clear if it exists
    rm -rf $SCRIPT_RELEVANT_PATH/$ARTIFACT_FOLDER_NAME
    # Create folder for artifacts 
    mkdir -p $ARTIFACT_FOLDER_NAME/$REPORTS_FOLDER_NAME
    # Create folder for tests 
    mkdir -p $ARTIFACT_FOLDER_NAME/test-results
popd

# Copy final jar file
cp -rf -v ${PROJECT_FOLDER}/composeApp/build/compose/jars/* ${SCRIPT_RELEVANT_PATH}/../${ARTIFACT_FOLDER_NAME}

# Print folder content
print_message "******** Log output folder *********"
ls -l $SCRIPT_RELEVANT_PATH/../$ARTIFACT_FOLDER_NAME

print_message "******** Finished *********"