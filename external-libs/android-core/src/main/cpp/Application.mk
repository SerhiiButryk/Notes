# Library application file
# Build type debug or release
ifeq ($(NDK_DEBUG),1)
    APP_OPTIM := debug
else
    APP_OPTIM := release
endif

# Find log output in build/intermediate/cxx/debug/x86/configure_stdout.txt
$(info [MY-LOGS] Entering Application.mk file in core module APP_OPTIM=$(APP_OPTIM))

# APP_ABI (armeabi-v7a arm64-v8a x86 x86_64 or all)
# commented as it is configure from gradle script
# APP_ABI := armeabi-v7a x86

APP_STL := c++_static
APP_CPPFLAGS := -std=c++17