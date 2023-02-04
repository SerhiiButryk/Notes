# Top level build script

TOP_LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

# Find log output in build/intermediate/cxx/debug/x86/configure_stdout.txt
$(info [MY-LOGS] Entering build_ndk.mk file)

# Module build file
include $(TOP_LOCAL_PATH)/../../../../../external-libs/android-core/src/main/cpp/Android.mk

# App build file
include $(TOP_LOCAL_PATH)/Android.mk