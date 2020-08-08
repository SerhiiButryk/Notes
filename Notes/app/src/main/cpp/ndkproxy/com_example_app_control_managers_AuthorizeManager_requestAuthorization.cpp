#include "com_example_app_control_managers_AuthorizeManager_requestAuthorization.h"

#include "utils/jstring.h"
#include "app/logic/base/event.h"
#include "app_common/types.h"
#include "app/logic/action_dispatcher.h"
#include "app/logic/receiver/system_event_receiver.h"
#include "app/logic/base/system_constants.h"

using namespace APP;

using MYLIB::JString;

#ifdef __cplusplus
extern "C" {
#endif

  JNIEXPORT void JNICALL Java_com_example_notes_test_control_managers_AuthorizeManager_requestAuthorization
  (JNIEnv* env, jobject, jstring jpassword, jstring jusername)
  {
       JString passwordString(env, jpassword);
       JString usernameString(env, jusername);

       Event event(SYSTEM_EVENT::AUTHORIZE);
       event.putData(PASSWORD_KEY, passwordString);
       event.putData(USERNAME_KEY, usernameString);

       SystemEventReceiver::getInstance()->forward(event);
  }

JNIEXPORT void JNICALL Java_com_example_notes_test_control_managers_AuthorizeManager_requestRegistration
  (JNIEnv* env, jobject, jstring jpassword, jstring jconfirm_password, jstring jusername)
  {
      JString passwordString(env, jpassword);
      JString usernameString(env, jusername);
      JString confirmpasswordString(env, jconfirm_password);

      Event event(SYSTEM_EVENT::REGISTER_ACCOUNT);
      event.putData(USERNAME_KEY, usernameString);
      event.putData(PASSWORD_KEY, passwordString);
      event.putData(CONFIRM_PASSWORD_KEY, confirmpasswordString);

      SystemEventReceiver::getInstance()->forward(event);
  }

JNIEXPORT void JNICALL Java_com_example_notes_test_control_managers_AuthorizeManager_requestUnlock
        (JNIEnv* env, jobject, jstring junlockKey)
{
      JString unlockKey(env, junlockKey);

      Event event(SYSTEM_EVENT::UNLOCK);
      event.putData(UNLOCK_KEY, unlockKey);

      SystemEventReceiver::getInstance()->forward(event);
}

JNIEXPORT void JNICALL Java_com_example_notes_test_control_managers_AuthorizeManager_requestBiometricLogin
        (JNIEnv *, jobject)
{
      // Authentication is done
    sendSystemAction(ACTION_TYPE::AUTHORIZATION_DONE);
}

JNIEXPORT void JNICALL Java_com_example_notes_test_control_managers_AuthorizeManager_requestUnlockKeystore
        (JNIEnv *, jobject)
{
    // Trigger action
    sendSystemAction(ACTION_TYPE::UNLOCK_KEYSTORE);
}

#ifdef __cplusplus
}
#endif
