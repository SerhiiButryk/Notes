#include "com_example_client_app_LoginActivity.h"

#include "app/logic/action_dispatcher.h"
#include "utils/log.h"
#include "app/logic/app_action.h"
#include "app/logic/base/env_constants.h"

#include <utility>

using namespace APP;

const static std::string TAG = "JNI";

#ifdef __cplusplus
extern "C" {
#endif

  JNIEXPORT void JNICALL Java_com_serhii_apps_notes_activities_AuthorizationActivity_initNative
  (JNIEnv* env, jobject jobj)
  {
        Log::Info("JNI", " %s IN", __FUNCTION__ );

        jclass cls = env->GetObjectClass(jobj);

        jmethodID _Login_Activity_onAuthorizedUser = env->GetMethodID(cls, "onAuthorizationFinished", "()V");
        jmethodID _Login_Activity_showAlertDialog = env->GetMethodID(cls, "showAlertDialog", "(I)V");
        jmethodID _Login_Activity_onRegisterdUser = env->GetMethodID(cls, "userRegistered", "()V");

        JavaVM* javaVm;
        int result = env->GetJavaVM(&javaVm);

        if (result != JNI_OK)
        {
            Log::Error(TAG, "initNative(): Failed to get JVM");
        }

        JNIWrapper callback_data(javaVm, jobj, _Login_Activity_onAuthorizedUser);
        JNIWrapper* callback_dialog = new JNIWrapper(javaVm, jobj, _Login_Activity_showAlertDialog);
        JNIWrapper* callback_registred = new JNIWrapper(javaVm, jobj, _Login_Activity_onRegisterdUser);

        AppAction::getInstance()->setAuthorizeCallback(std::move(callback_data));
        AppAction::getInstance()->setRegistrationCallback(callback_registred);
        AppAction::getInstance()->setShowDialogCallback(callback_dialog);

      Log::Info("JNI", " %s OUT", __FUNCTION__ );
  }

#ifdef __cplusplus
}
#endif
