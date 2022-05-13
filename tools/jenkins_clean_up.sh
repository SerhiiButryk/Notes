#!/bin/bash

# Copyright 2022. Happy coding ! :)
# Author: Serhii Butryk 

# This post build operations. Executing only for Jenkins.

# Fail if somthing is wrong
set -e

echo ""
echo "************ Cleaning up... ************"
echo ""

# Kill emulators if it's running
EMULATOR_LIST=$( ${ANDROID_SDK_ROOT}/emulator/emulator -list-avds )
if [[ -n $EMULATOR_LIST ]]
then
    echo "Found running emulators, killing..."
    
    # Stop all running emulators
    ${ANDROID_SDK_ROOT}/platform-tools/adb devices | grep emulator | cut -f1 | while read line; do adb -s $line emu kill; done
fi

echo ""
echo "*********** Finished ************"
echo ""