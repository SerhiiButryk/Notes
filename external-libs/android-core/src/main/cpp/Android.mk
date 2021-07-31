LOCAL_PATH:= $(call my-dir)

# Update if necessary
LIB_PATH := ../../../libs

# Module for OpenSSL library

    include $(CLEAR_VARS)
    LOCAL_MODULE := ssl_static
    LOCAL_SRC_FILES := $(LIB_PATH)/libs/arch-$(TARGET_ARCH_ABI)/lib/libssl.a
    include $(PREBUILT_STATIC_LIBRARY)

    include $(CLEAR_VARS)
    LOCAL_MODULE := crypto_static
    LOCAL_SRC_FILES := $(LIB_PATH)/libs/arch-$(TARGET_ARCH_ABI)/lib/libcrypto.a
    include $(PREBUILT_STATIC_LIBRARY)

# END

# Module for core library

    include $(CLEAR_VARS)

    LOCAL_MODULE := core

    # Core library source files

    LOCAL_SRC_FILES := ndkproxy/com_serhii_core_security_impl_HashAlgorithms.cpp \
                       ndkproxy/com_serhii_core_log_LogImpl.cpp \
                       ndkproxy/com_serhii_core_security_impl_crypto_CryptoOpenssl.cpp \
                       crypto/hash.cpp \
                       crypto/crypto_utils.cpp \
                       storage/file_system.cpp \
                       storage/system_storage.cpp \
                       storage/cashe_manager.cpp \
                       utils/algorithms.cpp \
                       utils/jni_wrpper.cpp \
                       utils/log.cpp \
                       utils/jstring.cpp \
                       utils/base64.cpp \
                       utils/jni_utils.cpp \

    LOCAL_CPP_FEATURES := rtti exceptions

    LOCAL_C_INCLUDES := $(LOCAL_PATH)/$(LIB_PATH)/
    LOCAL_C_INCLUDES += $(LOCAL_PATH)/$(LIB_PATH)/boost

    LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/
    LOCAL_EXPORT_C_INCLUDES += $(LOCAL_PATH)/$(LIB_PATH)/boost
    LOCAL_EXPORT_C_INCLUDES += $(LOCAL_PATH)/$(LIB_PATH)/

    LOCAL_CPPFLAGS += -std=c++17

    # Additional flag to link with log library at load time
    LOCAL_LDLIBS := -llog

    LOCAL_SHARED_LIBRARIES := libc++_shared
    LOCAL_STATIC_LIBRARIES := crypto_static ssl_static

    include $(BUILD_SHARED_LIBRARY)

# END