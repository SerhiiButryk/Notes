#!/usr/bin/env python3

import subprocess
import sys
import inspect

# 
# Utility script for Android
# v1.0.0 
#

# ********** CONSTANTS **************

DEBUG_MODE = False

# Predefined shell commands
android_text_input_command = ["adb", "shell", "input", "text"]

# ********** METHODS **************

# A method to show a hepl for this script
def showHelp():
    logI("Help for Android utility script:\n")
    logI("Usage: ./android.py [args...]\n")

    logM("-i, -input [text]"); logI(" - Enter a text in the focused view on the screen\n")
    logM("Example:"); logI(" ./android.py -i \"Some text\"\n")

# Find an arument for selected argument list
# Retruns the first found value or "" if nothing is found     
def getValueForArg(provided_args):

   found = False
   value = ""

   for arg in sys.argv:

        if found:
            value = arg    
            break

        for elem in provided_args:
            
            if elem == arg:
                found = True
                break

   return (value, found)
   
# Executes shell command   
def runShellCommand(args):
    logM("Executing command: " + str(args) + "\n")
    result = subprocess.run(args)
    logM("Done. Code: " + str(result.returncode) + "\n")

def logInternal(level, message):

    formatted = ""

    def getRed(mes): return "\033[91m {}\033[00m" .format(mes)
    def getGreen(mes): return "\033[92m {}\033[00m" .format(mes)

    if level == 1:
        # Error formatting
        formatted = getRed(message)
    elif level == 2:        
        # Main formatting
        formatted = getGreen(message)  
    else:
        # No formatting
        formatted = message      

    # Insert line number
    if DEBUG_MODE:
        lineNumber = inspect.stack()[1][2]
        print(lineNumber, formatted, end="")
    else:
        print(formatted, end="")

# Log info messages
def logI(message):
    logInternal(0, message)

# Log error messages
def logE(message):
    logInternal(1, message)

# Log main messages
def logM(message):
    logInternal(2, message)

# ********** START **************

# 1. Process arguments
input, android_text_input_args_found = getValueForArg(["-i", "-input"])

# 2. Execute selected command 
if android_text_input_args_found:
    logI("Entering text with adb")
    android_text_input_command.append(input)
    runShellCommand(android_text_input_command)
else:
    logE("Sorry, cannot execute this command. Please, make sure that the arguments are correct.\n")
    showHelp()

# ********** END **************