# Top level build script

TOP_LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

# Module build file
include $(TOP_LOCAL_PATH)/../../../../../external-libs/android-core/src/main/cpp/Android.mk
# App build file
include $(TOP_LOCAL_PATH)/Android.mk