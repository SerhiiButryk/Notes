#!/bin/bash

# Copyright 2022. Happy coding ! :)
# Author: Serhii Butryk 

# Simple script.
# 1) Build Notes App
# 2) Generates reports

# Fail if somthing is wrong
set -e

# Add untility scripts
SCRIPT_RELEVANT_PATH=$( dirname $BASH_SOURCE[0] )
. ${SCRIPT_RELEVANT_PATH}/utility_functions.sh

SCRIPT_ABSOLUTE_PATH="$( dirname $( pwd )$(cut -c 2- <<< $0) )"

PROJECT_FOLDER="${SCRIPT_ABSOLUTE_PATH}/../Notes"
APK_FILES_FOLDER="${SCRIPT_ABSOLUTE_PATH}/../Notes/app/build/outputs/apk"
MAPPING_FOLDER="${SCRIPT_ABSOLUTE_PATH}/../Notes/app/build/outputs/mapping/release"
ARTIFACT_FOLDER_NAME="Notes-App"
MAPPING_FOLDER_NAME="mapping"
REPORTS_FOLDER_NAME="reports"
DEBUGDATA_FOLDER_NAME="debugData"

echo ""
echo "******** Started building *********"
echo ""

# Build app
pushd ${PROJECT_FOLDER}
./gradlew clean assemble
popd

echo ""
echo "******** Finished *********"
echo ""

echo "******** Running static analysis *********"
echo ""

# Run Lint and SpotBugs static analysis
# About Lint: https://developer.android.com/studio/write/lint
# About SpotBugs: https://spotbugs.github.io/
pushd ${PROJECT_FOLDER}
./gradlew lint spotbugsDebug
popd

echo ""
echo "******** Finished *********"

echo "******** Copying files *********"
echo ""

pushd ${SCRIPT_ABSOLUTE_PATH}/../
# Clear if it exists
rm -rf $ARTIFACT_FOLDER_NAME
# Create folder for artifacts 
mkdir -p $ARTIFACT_FOLDER_NAME/${MAPPING_FOLDER_NAME}
mkdir -p $ARTIFACT_FOLDER_NAME/${REPORTS_FOLDER_NAME}/lint
mkdir -p $ARTIFACT_FOLDER_NAME/${REPORTS_FOLDER_NAME}/spotbugs
mkdir -p $ARTIFACT_FOLDER_NAME/${DEBUGDATA_FOLDER_NAME}
popd 

cp -rf -v ${APK_FILES_FOLDER}/* ${SCRIPT_ABSOLUTE_PATH}/../${ARTIFACT_FOLDER_NAME}
cp -rf -v ${MAPPING_FOLDER}/* ${SCRIPT_ABSOLUTE_PATH}/../${ARTIFACT_FOLDER_NAME}/${MAPPING_FOLDER_NAME}
# Copy reports
cp -rf -v ${PROJECT_FOLDER}/app/build/reports/* ${SCRIPT_ABSOLUTE_PATH}/../${ARTIFACT_FOLDER_NAME}/${REPORTS_FOLDER_NAME}/lint
cp -rf -v ${PROJECT_FOLDER}/app/build/spotbugs/* ${SCRIPT_ABSOLUTE_PATH}/../${ARTIFACT_FOLDER_NAME}/${REPORTS_FOLDER_NAME}/spotbugs

cp -rf -v ${PROJECT_FOLDER}/app/build/intermediates/ndkBuild/* $ARTIFACT_FOLDER_NAME/${DEBUGDATA_FOLDER_NAME}

echo ""
echo "******** Finished *********"
echo ""