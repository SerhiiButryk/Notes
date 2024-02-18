#!/usr/bin/env python3

import subprocess
import sys
import inspect
import os

# 
# Utility script for Android
#

# 
# A HELP 
# 
def printHelp():
    logI("Usage: ./android.py [args...]\n")

    logI(Green("-i, -input [text]") + " - Enters a text in the focused view on the screen\n");
    logI("Example: ./android.py -i \"Some text\"\n");

    logI(Green("-top-activity") + " - Prints current top activity\n");
    logI("Example: ./android.py -top-activity\n");

    logI(Green("-resign-apk") + " - Resigns apk with default keystore\n");
    logI("Example: ./android.py -resign-apk app_name.apk\n");

    logI(Green("-info") + " - Prints info about connected device\n");
    logI("Example: ./android.py -info\n");

    logI(Green("-enter-creads") + " - Enters the next 2 strings in the 2 text fileds if has focus\n");
    logI("Example: ./android.py -enter-creads email:password\n");

# 
# CONSTANTS and shell commands
# 

KEY_STORE_PATH = "/Users/sbutr/.android/debug.keystore"
BUILD_TOOLS = "/Users/sbutr/Library/Android/sdk/build-tools/34.0.0/"
KEY_PASS="android"
KEY_ALIAS="androiddebugkey"
DEBUG_MODE = False

android_text_input_command = ["adb", "shell", "input", "text"]
android_top_activity_command = ["adb", "shell", "dumpsys", "activity", "|", "grep", "mCurrentFocus=Window"]
android_zipalign_command = ["zipalign", "-p", "-f", "-v"]

#
#  Functions
# 

# Check if argumnets were passed and get the next a value if found
def hasCommand(args_list):
   value = ""
   found = False
   # Iterate over a list of arguments starting from 1 element
   for index, arg in enumerate(sys.argv[1:]):    
    for elem in args_list:
        if elem == arg:
            # If it has the next argument, then get it
            if (index + 1) < len(sys.argv[1:]):
                value = sys.argv[index+2]
            # An arg is found
            return (value, True)
   # An arg is not found     
   return (value, found) 
   
# Executes shell command   
def runCommand(command):
    runCommandInternal(command, False)

# Executes shell command
def runCommandWithLogs(command):
    runCommandInternal(command, True)

# Executes shell command
def runCommandInternal(command, printLogs):
    if printLogs:
        logI(Green("Executing command: " + str(command) + "\n"))
    result = subprocess.run(command)
    if printLogs:
        logI(Green("Done. Code: " + str(result.returncode) + "\n")) 

def logI(message):
    logInternal(0, message)

def logE(message):
    logInternal(1, message)

def logInternal(level, message):

    formatted = ""

    if level == 1:
        # Error formatting
        formatted = Red(message)
    elif level == 2:        
        # Main formatting
        formatted = Green(message)  
    else:
        # No formatting
        formatted = message      

    # Insert line number
    if DEBUG_MODE:
        lineNumber = inspect.stack()[1][2]
        print(lineNumber, formatted, end="")
    else:
        print(formatted, end="")

def Red(mes): return "\033[91m {}\033[00m" .format(mes)
def Green(mes): return "\033[92m {}\033[00m" .format(mes)

# 
# Start  
# 

# Parse arguments and run commands

TEXT, COMMAND_ENTER_TEXT = hasCommand(["-i", "-input"])

if COMMAND_ENTER_TEXT:
    logI("Entering text:\n")
    android_text_input_command.append(TEXT)
    runCommandWithLogs(android_text_input_command)
    # End the program
    sys.exit()

_, COMMAND_SHOW_TOP_ACTIVITY = hasCommand(["-top-activity"])

if COMMAND_SHOW_TOP_ACTIVITY:
    logI("Top activity:\n")
    runCommand(android_top_activity_command)
    # End the program
    sys.exit()

_, COMMAND_DEVICE_INFO = hasCommand(["-info"])

# Extend if neccessary
if COMMAND_DEVICE_INFO:

    logI(Green("Release version:\n"))   
    runCommand(["adb", "shell", "getprop", "ro.build.version.release"])

    logI(Green("Release or code name version:\n"))
    runCommand(["adb", "shell", "getprop", "ro.build.version.release_or_codename"])

    logI(Green("Build ID:\n"))   
    runCommand(["adb", "shell", "getprop", "ro.build.id"])

    logI(Green("Manufacturer:\n"))   
    runCommand(["adb", "shell", "getprop", "ro.product.manufacturer"])

    logI(Green("Device model:\n"))   
    runCommand(["adb", "shell", "getprop", "ro.product.model"])

    logI(Green("Supported ABI list:\n"))
    runCommand(["adb", "shell", "getprop", "ro.product.cpu.abilist"])

    logI(Green("SDK version:\n"))   
    runCommand(["adb", "shell", "getprop", "ro.build.version.sdk"])

    # End the program
    sys.exit()

TEXT, COMMAND_ENTER_CREDENTIALS = hasCommand(["-enter-creads"])

if COMMAND_ENTER_CREDENTIALS:

    strings = TEXT.split(':')

    logI("Entering text:\n")

    runCommand(["adb", "shell", "input", "text", strings[0]])
    runCommand(["adb", "shell", "input", "keyevent", "66"])
    runCommand(["adb", "shell", "input", "text", strings[1]])
    runCommand(["adb", "shell", "input", "keyevent", "66"])

    # End the program
    sys.exit()

APK_NAME, COMMAND_RESIGN_APK = hasCommand(["-resign-apk"])

if COMMAND_RESIGN_APK and APK_NAME:
    
    # Steps to resign app:
    # 1. Unzip.
    # 2. Modify if neccessary.
    # 3. Remove META-INF
    # 4. Zip
    # 5. Run zipalign
    # 6. Resign with selected keystore  
    
    print("Unzipping...")

    # Make temp dir
    runCommand(["rm", "-rf", "temp"])
    runCommand(["mkdir", "temp"])

    # Unzip
    runCommand(["unzip", "-q", APK_NAME, "-d", "temp"])

    # Remove META-INF/
    runCommand(["rm", "-rf", "temp/META-INF"])

    print("Zipping...")

    # Zip
    os.chdir("temp")
    runCommand(["zip", "-q", "-0", "-r", "../temp.apk", ".", "-i", "*"])
    os.chdir("..")

    print("Signing...")

    # Zipalign
    runCommand([BUILD_TOOLS + "zipalign", "-p", "-f", "4", "temp.apk", "out.apk"])

    # Sign
    runCommand([BUILD_TOOLS + "apksigner", "sign", "--ks", KEY_STORE_PATH, "--ks-pass", "pass:" + KEY_PASS, "--ks-key-alias", KEY_ALIAS, "out.apk"])

    print("Done")

    # End the program
    sys.exit()

# Looks like provided args are incorrect, so show a help
logE("Sorry, cannot execute this command. Please, make sure that the arguments are correct.\n")
printHelp()

# End 
