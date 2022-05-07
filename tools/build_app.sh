#!/bin/bash

# Copyright 2022. Happy coding ! :)
# Author: Serhii Butryk 

SCRIPT_ABSOLUTE_PATH="$( dirname $( pwd )$(cut -c 2- <<< $0) )"

./${SCRIPT_ABSOLUTE_PATH}/../Notes/gradlew assemble