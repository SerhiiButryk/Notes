#!/bin/bash

# Copyright 2022. Happy coding ! :)
# Author: Serhii Butryk 

SCRIPT_RELEVANT_PATH=$( dirname $BASH_SOURCE[0] )

# Build project
${SCRIPT_RELEVANT_PATH}/build_app.sh --bundle

# Test project
${SCRIPT_RELEVANT_PATH}/run_tests.sh