LOCAL_PATH:= $(call my-dir)

# Update if necessary
SDK_PATH := ../../../../../sdk
SDK_3TH_PARTIES := $(SDK_PATH)/3th_parties

# Local module

    include $(CLEAR_VARS)

    LOCAL_MODULE := rabbit

    # App cpp source files

    LOCAL_SRC_FILES := ndkproxy/com_example_app_control_managers_AuthorizeManager_requestAuthorization.cpp \
                       ndkproxy/com_example_client_app_LoginActivity.cpp \
                       ndkproxy/com_example_client_app_MainActivity.cpp \
                       ndkproxy/com_example_client_app_control_NativeBridge.cpp \
                       app/logic/receiver/system_event_receiver.cpp \
                       app/logic/system/common_constants.cpp \
                       app/net/server_agent.cpp \
                       app/logic/event_dispatcher.cpp \
                       app/logic/event/authorize_event.cpp \
                       app/logic/event/registration_event.cpp \
                       app/core/login_action_sender.cpp \
                       app/core/utils/auth_utils.cpp \
                       app/logic/handler/authorize_handler.cpp \
                       app/logic/handler/registration_handler.cpp \
                       app/logic/handler/unlock_handler.cpp \
                       app_common/env_constants.cpp \

    LOCAL_C_INCLUDES := $(LOCAL_PATH)/$(SDK_PATH)
    LOCAL_C_INCLUDES += $(LOCAL_PATH)

    # Include system libraries

    LOCAL_LDLIBS := -llog

    # Include local build libraries

    LOCAL_SHARED_LIBRARIES := libc++_shared socket core

    include $(BUILD_SHARED_LIBRARY)

# END
