#!/bin/bash

# Copyright 2022. Happy coding ! :)
# Author: Serhii Butryk 

# This is simple script to start Jenkins on Linux machine.
# Before running this script you should install Jenkins on your machine
# and check if all requirements are met.

# Fail if somthing is wrong
set -e

# Add untility scripts
SCRIPT_RELEVANT_PATH=$( dirname $BASH_SOURCE[0] )
. ${SCRIPT_RELEVANT_PATH}/utility_functions.sh

# Check if running on a Linux machine 
check_and_exit_if_not_running_on_linux

# If no arguments then exit
check_and_exit_if_no_args_provided() {
    if [ $# -eq 0 ]
    then
        print_error "Error: no arguments (E-1012)" 
        exit 1
    fi
}

# show help
help() {
    print_message "HELP:"
    print_message "*****************************************************"
    print_message "Script for running Jenkins locally on Linux machine"
    print_message "*****************************************************"
    print_message "Available options:"
    print_message "--init        - init Jenkins"
    print_message "--start       - start Jenkins"
    print_message "--stop        - stop Jenkins"
    print_message "--status      - current status of Jenkins"
    print_message "--password    - shows unlock password"
    print_message "*****************************************************"
}

# Local variables 
FLAG_INIT_JENKINS=false
FLAG_START_JENKINS=false
FLAG_STOP_JENKINS=false
FLAG_GET_STATUS_JENKINS=false

CURRENT_PATH="$(pwd)"
JENKINS_DATA_FILES_PATH="$(pwd)/../jenkins"
JENKINS_HOME_DIR_PATH="/var/lib/jenkins"

# parse arguments 
while getopts ":h-:" OPTION
do 
    case $OPTION in
    -)  
        case $OPTARG in 
            init)
                FLAG_INIT_JENKINS=true
                ;;
            start)
                FLAG_START_JENKINS=true
                ;;
            stop)    
                FLAG_STOP_JENKINS=true
                ;;
            status)    
                FLAG_GET_STATUS_JENKINS=true
                ;;
            h|help)
                help
                exit 1    
                ;;
            password)
                print_message "Unlock password:"
                # Get unlock password
                sudo cat /var/lib/jenkins/secrets/initialAdminPassword
                exit 1    
                ;;
        esac    
        ;;  
    h) 
        help
        exit 1    
        ;;   
    \?) 
        print_error "Error: invalid arguments" 
        help
        exit 1
        ;;   
    esac    
done
shift $((OPTIND - 1))

if [ "$FLAG_START_JENKINS" = true ];
then
    print_message "> Start Jenkins"
    # Run as super user
    sudo systemctl start jenkins

    print_message "> Open browser"
    # Open browser
    xdg-open http://localhost:8080


elif [ "$FLAG_STOP_JENKINS" = true ];
then
    print_message "> Stop Jenkins"
    # Run as super user
    sudo systemctl stop jenkins


elif [ "$FLAG_GET_STATUS_JENKINS" = true ];
then
    print_message "> Status of Jenkins"
    # Run as super user
    sudo systemctl status jenkins

elif [ "$FLAG_INIT_JENKINS" = true ];
then
    print_message "> Init Jenkins"
    
    print_message "> Will copy next files (from $JENKINS_DATA_FILES_PATH to $JENKINS_HOME_DIR_PATH):"
    
    echo ""
    
    ls -l $JENKINS_DATA_FILES_PATH

    echo ""

    print_message "!!! ATTENTION !!! You files will be deleted in $JENKINS_HOME_DIR_PATH"

    echo ""

    read -p "Do you want to proceed? (yes/no) " yn

    case $yn in 
        yes) 
            ;;
        no)
            echo Exiting...
            exit 1
            ;;
        * ) echo Invalid response;
            exit 1
            ;;
    esac

    echo ""

    print_message "> Working..."

    # Go to Jenkins Home dir
    pushd $JENKINS_HOME_DIR_PATH > /dev/null
    
    # Delete all files
    sudo find . -delete

    # Copy files 
    sudo cp -rf $JENKINS_DATA_FILES_PATH/* ./ 

    # Set up correct permissions
    sudo find . -exec chmod 777 {} +
    sudo find . -exec chown jenkins:jenkins {} +
    
    # Go back
    popd > /dev/null

    print_message "> Completed"

else 
    print_error "> Invalid commend received. Nothing to do."
fi