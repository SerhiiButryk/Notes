#!/bin/bash

# Copyright 2026. Happy coding ! :)
# Author: Serhii Butryk 

SCRIPT_RELEVANT_PATH=$( dirname $BASH_SOURCE[0] )

chmod +x ./build_app.sh

# Build project
${SCRIPT_RELEVANT_PATH}/build_app.sh --bundle --apk