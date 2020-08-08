#include "com_example_client_app_NotesViewActivity.h"

#include <string>
#include <utility>

#include "app/core/app_action_sender.h"
#include "storage/file_system.h"
#include "utils/jstring.h"
#include "utils/log.h"

using namespace APP;
using namespace MYLIB;

namespace {
    const std::string TAG = "JNI";
}

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_example_notes_test_NotesViewActivity_initNativeConfigs
  (JNIEnv* env, jobject obj, jstring jstr)
{
    JString filePath(env, jstr);

    FileSystem::getInstance()->initFilePath(filePath);

    jclass clz = env->GetObjectClass(obj);

    JavaVM* javaVm;
    env->GetJavaVM(&javaVm);

    jmethodID id = env->GetMethodID(clz, "onAuthorization", "()V");

    JNIWrapper callback(javaVm, obj, id);

    AppActionSender::getInstance()->addAuthorizeCallback(std::move(callback));
}

JNIEXPORT void JNICALL Java_com_example_notes_test_NotesViewActivity_notifyOnStop
   (JNIEnv *, jobject)
{
    Log::Info(TAG, "Java_com_example_notes_test_NotesViewActivity_notifyOnStop \n");

    AppActionSender::getInstance()->removeUnlockKeystoreNoteViewCallback();
}

JNIEXPORT void JNICALL Java_com_example_notes_test_NotesViewActivity_notifyOnResume
        (JNIEnv* env, jobject obj)
{
    Log::Info(TAG, "Java_com_example_notes_test_NotesViewActivity_notifyOnResume \n");

    jclass clz = env->GetObjectClass(obj);

    JavaVM* javaVm;
    env->GetJavaVM(&javaVm);

    jmethodID idKeystore = env->GetMethodID(clz, "onUnlockKeystore", "()V");

    JNIWrapper* unlockKeystoreCallback = new JNIWrapper(javaVm, obj, idKeystore);

    AppActionSender::getInstance()->setUnlockKeystoreNoteViewCallback(unlockKeystoreCallback);
}

#ifdef __cplusplus
}
#endif
