LOCAL_PATH:= $(call my-dir)

# Update if necessary
SDK_PATH := ../../../../../sdk
SDK_3TH_PARTIES := $(SDK_PATH)/3th_parties

# Module for socket library

    include $(CLEAR_VARS)

    LOCAL_MODULE := socket

    # Socket library source files

    LOCAL_SRC_FILES := $(SDK_PATH)/net/socket.cpp \
                       $(SDK_PATH)/common/log.cpp \
                       $(SDK_PATH)/common/exception.cpp \
                       $(SDK_PATH)/net/constants.cpp \

    LOCAL_CPP_FEATURES := rtti exceptions

    include $(BUILD_SHARED_LIBRARY)

# END

# Module for OpenSSL library

    include $(CLEAR_VARS)
    LOCAL_MODULE := ssl_static
    LOCAL_SRC_FILES := $(SDK_3TH_PARTIES)/openssl/libs/arch-$(TARGET_ARCH_ABI)/lib/libssl.a
    include $(PREBUILT_STATIC_LIBRARY)

    include $(CLEAR_VARS)
    LOCAL_MODULE := crypto_static
    LOCAL_SRC_FILES := $(SDK_3TH_PARTIES)/openssl/libs/arch-$(TARGET_ARCH_ABI)/lib/libcrypto.a
    include $(PREBUILT_STATIC_LIBRARY)

# END

# Module for core library

    include $(CLEAR_VARS)

    LOCAL_MODULE := core

    # Core library source files

    LOCAL_SRC_FILES := ndkproxy/com_example_core_security_impl_HashAlgorithms.cpp \
                       ndkproxy/com_example_core_common_log_impl_LogImpl.cpp \
                       $(SDK_PATH)/crypto/hash.cpp \
                       storage/file_system.cpp \
                       storage/system_storage.cpp \
                       storage/cashe_manager.cpp \
                       utils/algorithms.cpp \
                       utils/jni_wrpper.cpp \
                       utils/log.cpp \
                       utils/jstring.cpp \

    LOCAL_CPP_FEATURES := rtti exceptions

    LOCAL_C_INCLUDES := $(LOCAL_PATH)/$(SDK_3TH_PARTIES)/openssl
    LOCAL_C_INCLUDES += $(LOCAL_PATH)/$(SDK_3TH_PARTIES)/boost
    LOCAL_C_INCLUDES += $(LOCAL_PATH)/$(SDK_PATH)

    LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/
    LOCAL_EXPORT_C_INCLUDES += $(LOCAL_PATH)/$(SDK_3TH_PARTIES)/boost
    LOCAL_EXPORT_C_INCLUDES += $(LOCAL_PATH)/$(SDK_3TH_PARTIES)/openssl

    LOCAL_CPPFLAGS += -std=c++17

    LOCAL_LDLIBS := -llog
    LOCAL_SHARED_LIBRARIES := libc++_shared
    LOCAL_STATIC_LIBRARIES := crypto_static ssl_static

    include $(BUILD_SHARED_LIBRARY)

# END