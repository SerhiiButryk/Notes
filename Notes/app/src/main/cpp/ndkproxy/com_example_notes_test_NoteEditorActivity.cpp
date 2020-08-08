#include "com_example_notes_test_NoteEditorActivity.h"

#include "app/core/app_action_sender.h"
#include "utils/jstring.h"
#include "utils/log.h"

using namespace APP;
using namespace MYLIB;

#ifdef __cplusplus
extern "C" {
#endif

  JNIEXPORT void JNICALL Java_com_example_notes_test_NoteEditorActivity_initNativeConfigs
  (JNIEnv* env, jobject obj)
{
  jclass clz = env->GetObjectClass(obj);

  jmethodID idKeystore = env->GetMethodID(clz, "onUnlockKeystore", "()V");

  JavaVM* javaVm;
  env->GetJavaVM(&javaVm);

  JNIWrapper* unlockKeystoreCallback = new JNIWrapper(javaVm, obj, idKeystore);

  AppActionSender::getInstance()->setUnlockKeystoreEditorViewCallback(unlockKeystoreCallback);
}

JNIEXPORT void JNICALL Java_com_example_notes_test_NoteEditorActivity_notifyOnDestroy
  (JNIEnv *, jobject)
{
    Log::Info("JNI", " Java_com_example_notes_test_NoteEditorActivity_notifyOnDestroy \n");

  AppActionSender::getInstance()->removeUnlockKeystoreEditorViewCallback();
}

#ifdef __cplusplus
}
#endif
