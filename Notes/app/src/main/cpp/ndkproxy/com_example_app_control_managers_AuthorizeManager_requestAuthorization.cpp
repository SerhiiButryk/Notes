#include "com_example_app_control_managers_AuthorizeManager_requestAuthorization.h"

#include "utils/jstring.h"
#include "app/logic/base/event.h"
#include "app/logic/base/types.h"
#include "app/logic/action_dispatcher.h"
#include "app/logic/receiver/system_event_receiver.h"
#include "app/logic/base/system_constants.h"
#include "utils/log.h"
#include "app/logic/utils/auth_utils.h"

using namespace APP;
using namespace MYLIB;

#ifdef __cplusplus
extern "C" {
#endif

  JNIEXPORT jboolean JNICALL Java_com_serhii_apps_notes_control_auth_AuthManager_requestAuthorization__Ljava_lang_String_2Ljava_lang_String_2
  (JNIEnv* env, jobject, jstring jpassword, jstring jusername)
  {
       Info("JNI", " %s IN", __FUNCTION__ );

       JString passwordString(env, jpassword);
       JString usernameString(env, jusername);

       Event event(SYSTEM_EVENT::AUTHORIZE);
       event.putData(PASSWORD_KEY, passwordString);
       event.putData(USERNAME_KEY, usernameString);

       bool result = SystemEventReceiver::getInstance()->forward(event);

      Info("JNI", " %s OUT", __FUNCTION__ );

      return result;
  }

JNIEXPORT jboolean JNICALL Java_com_serhii_apps_notes_control_auth_AuthManager_requestRegistration
  (JNIEnv* env, jobject, jstring jpassword, jstring jconfirm_password, jstring jusername)
  {
      Info("JNI", " %s IN", __FUNCTION__ );

      JString passwordString(env, jpassword);
      JString usernameString(env, jusername);
      JString confirmpasswordString(env, jconfirm_password);

      Event event(SYSTEM_EVENT::REGISTER_ACCOUNT);
      event.putData(USERNAME_KEY, usernameString);
      event.putData(PASSWORD_KEY, passwordString);
      event.putData(CONFIRM_PASSWORD_KEY, confirmpasswordString);

      bool result = SystemEventReceiver::getInstance()->forward(event);

      Info("JNI", " %s OUT", __FUNCTION__ );

      return result;
  }

JNIEXPORT void JNICALL Java_com_serhii_apps_notes_control_auth_AuthManager_requestUnlock
        (JNIEnv* env, jobject, jstring junlockKey, jstring jcurrentKey)
{
      Info("JNI", " %s IN", __FUNCTION__ );

      JString unlockKey(env, junlockKey);
      JString currentKey(env, jcurrentKey);

      Event event(SYSTEM_EVENT::UNLOCK);
      event.putData(UNLOCK_KEY, unlockKey);
      event.putData(CURRENT_UNLOCK_KEY, currentKey);

      SystemEventReceiver::getInstance()->forward(event);

      Info("JNI", " %s OUT", __FUNCTION__ );
}

JNIEXPORT void JNICALL Java_com_serhii_apps_notes_control_auth_AuthManager_requestAuthorization__
        (JNIEnv *, jobject)
{
    Info("JNI", " %s IN", __FUNCTION__ );

    // Authentication is done
    ActionDispatcher::getInstance()->sendMessage(SYSTEM_MESSAGE::AUTHORIZATION_DONE);

    Info("JNI", " %s OUT", __FUNCTION__ );
}

JNIEXPORT jboolean JNICALL Java_com_serhii_apps_notes_control_auth_AuthManager_verifyInput
        (JNIEnv * env, jobject, jstring jpassword, jstring jconfirm_password, jstring jusername)
{
    Info("JNI", " %s IN", __FUNCTION__ );

    JString passwordString(env, jpassword);
    JString usernameString(env, jusername);
    JString confirmpasswordString(env, jconfirm_password);

    auto success = AuthUtils::checkRules(passwordString, confirmpasswordString, usernameString, true);

    bool result = false;
    if (success == SYSTEM_MESSAGE ::NO_ERRORS)
    {
        result = true;
    } else {
        Info("JNI", " %s sending message OUT", __FUNCTION__);

        ActionDispatcher::getInstance()->sendMessage(success);
    }

    Info("JNI", " %s res = %d OUT", __FUNCTION__, result);

    return result;
}

#ifdef __cplusplus
}
#endif
