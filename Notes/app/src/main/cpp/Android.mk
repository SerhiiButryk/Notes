LOCAL_PATH:= $(call my-dir)

# Local module

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
                       app/logic/event/authorize_event.cpp \
                       app/logic/event/register_event.cpp \
                       app/logic/app_action.cpp \
                       app/logic/utils/auth_utils.cpp \
                       app/logic/handler/authorize_handler.cpp \
                       app/logic/handler/register_handler.cpp \
                       app/logic/handler/unlock_handler.cpp \
                       app/logic/base/env_constants.cpp \

    LOCAL_C_INCLUDES += $(LOCAL_PATH)

    # Include system libraries

    LOCAL_LDLIBS := -llog

    # Include local build libraries

    LOCAL_SHARED_LIBRARIES := libc++_shared core

    include $(BUILD_SHARED_LIBRARY)

# END
