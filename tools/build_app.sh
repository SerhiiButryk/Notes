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

PROJECT_FOLDER="${SCRIPT_RELEVANT_PATH}/../Notes"
APK_FILES_FOLDER="${SCRIPT_RELEVANT_PATH}/../Notes/app/build/outputs/apk"
BUNDLE_FILES_FOLDER="${SCRIPT_RELEVANT_PATH}/../Notes/app/build/outputs/bundle"
MAPPING_FOLDER="${SCRIPT_RELEVANT_PATH}/../Notes/app/build/outputs/mapping/release"

ARTIFACT_FOLDER_NAME="Notes-App"
MAPPING_FOLDER_NAME="mapping"
REPORTS_FOLDER_NAME="reports"
DEBUGDATA_FOLDER_NAME="debugData"
BUNDLDATA_FOLDER_NAME="bundle"

BUILD_APK=false

while [ : ]; do
  case "$1" in
    --apk)
        BUILD_APK=true
        shift
        ;;
    --bundle)
        BUILD_APK=false
        shift
        ;;
  esac
  break
done

print_message "******** Selecting release key *********"

pushd ${PROJECT_FOLDER}/app
sed 's+test_only.jks+../../../notes-app-release-key.jks+g' build.gradle > build.gradle.cp 
rm build.gradle 
mv build.gradle.cp build.gradle
popd

print_message "******** Started building *********"

if [ "$BUILD_APK" == true ]
then 

    print_message "******** Building APK *********"

    # Build apk
    pushd ${PROJECT_FOLDER}
    ./gradlew clean assemble
    popd

else

    print_message "******** Building Bundle *********"
    
    # Build app bundle
    pushd ${PROJECT_FOLDER}
    ./gradlew clean bundle
    popd

fi

print_message "******** Finished *********"

print_message "******** Running static analysis *********"

# Run Lint and SpotBugs static analysis
# About Lint: https://developer.android.com/studio/write/lint
# About SpotBugs: https://spotbugs.github.io/
pushd ${PROJECT_FOLDER}
./gradlew lint spotbugsDebug
popd

print_message "******** Finished *********"

print_message "******** Copying files *********"

pushd ${SCRIPT_RELEVANT_PATH}/../

# Clear if it exists
rm -rf ${SCRIPT_RELEVANT_PATH}/$ARTIFACT_FOLDER_NAME

# Create folder for artifacts 
mkdir -p $ARTIFACT_FOLDER_NAME/${MAPPING_FOLDER_NAME}
mkdir -p $ARTIFACT_FOLDER_NAME/${REPORTS_FOLDER_NAME}/lint
mkdir -p $ARTIFACT_FOLDER_NAME/${REPORTS_FOLDER_NAME}/spotbugs
mkdir -p $ARTIFACT_FOLDER_NAME/${DEBUGDATA_FOLDER_NAME}
mkdir -p $ARTIFACT_FOLDER_NAME/${BUNDLDATA_FOLDER_NAME}
popd 

cp -rf -v ${APK_FILES_FOLDER}/* ${SCRIPT_RELEVANT_PATH}/../${ARTIFACT_FOLDER_NAME}
cp -rf -v ${MAPPING_FOLDER}/* ${SCRIPT_RELEVANT_PATH}/../${ARTIFACT_FOLDER_NAME}/${MAPPING_FOLDER_NAME}

if [ "$BUILD_APK" == false ]
then
    cp -rf -v ${BUNDLE_FILES_FOLDER}/* ${SCRIPT_RELEVANT_PATH}/../${ARTIFACT_FOLDER_NAME}/${BUNDLDATA_FOLDER_NAME}
fi  

# Copy reports
cp -rf -v ${PROJECT_FOLDER}/app/build/reports/* ${SCRIPT_RELEVANT_PATH}/../${ARTIFACT_FOLDER_NAME}/${REPORTS_FOLDER_NAME}/lint
cp -rf -v ${PROJECT_FOLDER}/app/build/spotbugs/* ${SCRIPT_RELEVANT_PATH}/../${ARTIFACT_FOLDER_NAME}/${REPORTS_FOLDER_NAME}/spotbugs

cp -rf -v ${PROJECT_FOLDER}/app/build/intermediates/ndkBuild/* $ARTIFACT_FOLDER_NAME/${DEBUGDATA_FOLDER_NAME}

print_message "******** Finished *********"