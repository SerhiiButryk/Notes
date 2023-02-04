LOCAL_PATH:= $(call my-dir)

$(info [MY-LOGS] Entering Android.mk file)

# START app build settings

    include $(CLEAR_VARS)

    LOCAL_MODULE := rabbit

    # App cpp source files

    LOCAL_SRC_FILES := ndkproxy/com_example_app_control_managers_AuthorizeManager_requestAuthorization.cpp \
                       ndkproxy/com_example_client_app_LoginActivity.cpp \
                       ndkproxy/com_example_client_app_NotesViewActivity.cpp \
                       ndkproxy/com_example_client_app_control_NativeBridge.cpp \
                       app/logic/receiver/system_event_receiver.cpp \
                       app/logic/base/system_constants.cpp \
                       app/logic/action_dispatcher.cpp \
                       app/logic/app_action.cpp \
                       app/logic/utils/auth_utils.cpp \
                       app/logic/handler/authorize_handler.cpp \
                       app/logic/handler/register_handler.cpp \
                       app/logic/handler/unlock_handler.cpp \
                       app/logic/base/env_constants.cpp \

    # Headers are included
    LOCAL_C_INCLUDES += $(LOCAL_PATH)

    # System libraries are included
    LOCAL_LDLIBS := -llog

    # Local libraries are included
    LOCAL_SHARED_LIBRARIES := libc++_shared core

    include $(BUILD_SHARED_LIBRARY)

# END
