#!/bin/bash

# Copyright 2022. Happy coding ! :)
# Author: Serhii Butryk 

# File contains utility functions for Shell scrpiting
# 
# You can use it by executing this Shell script :
#
# SCRIPT_RELEVANT_PATH=$( dirname $BASH_SOURCE[0] )
# . ${SCRIPT_RELEVANT_PATH}/utility_functions.sh
#
# This ensures that scripts are correctly called

# Prints text in green color
print_message() {

    # https://stackoverflow.com/questions/5947742/how-to-change-the-output-color-of-echo-in-linux
    GREEN_COLOR='\033[0;32m'
    RESET_COLOR='\033[0m'

    # Format passed text
    printf "${GREEN_COLOR}$1${RESET_COLOR}\n"

}

# Prints text in red color
print_error() {
    
    # https://stackoverflow.com/questions/5947742/how-to-change-the-output-color-of-echo-in-linux
    RED_COLOR='\033[0;31m'
    RESET_COLOR='\033[0m'

    # Format passed text
    printf "${RED_COLOR}$1${RESET_COLOR}\n"

}

# Print script debug information
print_script_debug_info() {
    
    # local variables
    local script_file_name=$0
    local script_working_directory=$(pwd)

    local machine_name=$(uname)
    local machine_arch=$(uname -m)

    local param1="Running script: $script_file_name"
    local param2="From directory: $script_working_directory"
    local param3="On machine: $machine_name $machine_arch"

    # Print info about this script
    print_message "${param1}\n${param2}\n${param3}"
}

# Check and exit if not a linux machine
check_and_exit_if_not_running_on_linux() {
    local machine_name=$(uname)
    if [ $machine_name != "Linux" ]
    then
        print_error "Error: you are not on a Linux machine, can't run commands."
        exit 1
    fi
}
