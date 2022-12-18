#include "com_example_app_control_managers_AuthorizeManager_requestAuthorization.h"

#include "utils/jstring.h"
#include "app/logic/base/event.h"
#include "app/logic/base/types.h"
#include "app/logic/action_dispatcher.h"
#include "app/logic/receiver/system_event_receiver.h"
#include "app/logic/base/system_constants.h"
#include "utils/log.h"

using namespace APP;
using namespace MYLIB;

#ifdef __cplusplus
extern "C" {
#endif

  JNIEXPORT void JNICALL Java_com_serhii_apps_notes_control_auth_AuthManager_requestAuthorization
  (JNIEnv* env, jobject, jstring jpassword, jstring jusername)
  {
       Log::Info("JNI", " %s IN", __FUNCTION__ );

       JString passwordString(env, jpassword);
       JString usernameString(env, jusername);

       Event event(SYSTEM_EVENT::AUTHORIZE);
       event.putData(PASSWORD_KEY, passwordString);
       event.putData(USERNAME_KEY, usernameString);

       SystemEventReceiver::getInstance()->forward(event);

      Log::Info("JNI", " %s OUT", __FUNCTION__ );
  }

JNIEXPORT void JNICALL Java_com_serhii_apps_notes_control_auth_AuthManager_requestRegistration
  (JNIEnv* env, jobject, jstring jpassword, jstring jconfirm_password, jstring jusername)
  {
      Log::Info("JNI", " %s IN", __FUNCTION__ );

      JString passwordString(env, jpassword);
      JString usernameString(env, jusername);
      JString confirmpasswordString(env, jconfirm_password);

      Event event(SYSTEM_EVENT::REGISTER_ACCOUNT);
      event.putData(USERNAME_KEY, usernameString);
      event.putData(PASSWORD_KEY, passwordString);
      event.putData(CONFIRM_PASSWORD_KEY, confirmpasswordString);

      SystemEventReceiver::getInstance()->forward(event);

      Log::Info("JNI", " %s OUT", __FUNCTION__ );
  }

JNIEXPORT void JNICALL Java_com_serhii_apps_notes_control_auth_AuthManager_requestUnlock
        (JNIEnv* env, jobject, jstring junlockKey, jstring jcurrentKey)
{
      Log::Info("JNI", " %s IN", __FUNCTION__ );

      JString unlockKey(env, junlockKey);
      JString currentKey(env, jcurrentKey);

      Event event(SYSTEM_EVENT::UNLOCK);
      event.putData(UNLOCK_KEY, unlockKey);
      event.putData(CURRENT_UNLOCK_KEY, currentKey);

      SystemEventReceiver::getInstance()->forward(event);

      Log::Info("JNI", " %s OUT", __FUNCTION__ );
}

JNIEXPORT void JNICALL Java_com_serhii_apps_notes_control_auth_AuthManager_requestBiometricLogin
        (JNIEnv *, jobject)
{
    Log::Info("JNI", " %s IN", __FUNCTION__ );

    // Authentication is done
    ActionDispatcher::getInstance()->sendMessage(SYSTEM_MESSAGE::AUTHORIZATION_DONE);

    Log::Info("JNI", " %s OUT", __FUNCTION__ );
}

#ifdef __cplusplus
}
#endif
