#include "com_example_client_app_LoginActivity.h"

#include "app/logic/event_dispatcher.h"
#include "app/net/server_agent.h"
#include "utils/log.h"
#include "app/core/login_action_sender.h"
#include "app_common/env_constants.h"

#include <utility>

using namespace APP;

const static std::string TAG = "JNI";

#ifdef __cplusplus
extern "C" {
#endif

  JNIEXPORT void JNICALL Java_com_example_notes_test_AuthorizationActivity_initNative
  (JNIEnv* env, jobject jobj)
  {
        jclass cls = env->GetObjectClass(jobj);

        jmethodID _Login_Activity_onAuthorizedUser = env->GetMethodID(cls, "onAuthorizationFinished", "()V");
        jmethodID _Login_Activity_showAlertDialog = env->GetMethodID(cls, "showAlertDialog", "(I)V");
        jmethodID _Login_Activity_onRegisterdUser = env->GetMethodID(cls, "showRegistrationUI", "()V");

        JavaVM* javaVm;
        int result = env->GetJavaVM(&javaVm);

        if (result != JNI_OK)
        {
            Log::Error(TAG, "initNative(): Failed to get JVM");
        }

        JNIWrapper callback_data(javaVm, jobj, _Login_Activity_onAuthorizedUser);
        JNIWrapper callback_dialog(javaVm, jobj, _Login_Activity_showAlertDialog);
        JNIWrapper callback_registred(javaVm, jobj, _Login_Activity_onRegisterdUser);

        LoginActionSender::getInstance()->addAuthorizeCallback(std::move(callback_data));
        LoginActionSender::getInstance()->addRegistrationCallback(std::move(callback_registred));
        LoginActionSender::getInstance()->addShowDialogCallback(std::move(callback_dialog));

        EventDispatcher::getInstance()->addAuthorizeEventObserver(LoginActionSender::getInstance());
        EventDispatcher::getInstance()->addRegistrationEventObserver(
                LoginActionSender::getInstance());
        EventDispatcher::getInstance()->addShowDialogEventObserver(LoginActionSender::getInstance());
  }

#ifdef __cplusplus
}
#endif
