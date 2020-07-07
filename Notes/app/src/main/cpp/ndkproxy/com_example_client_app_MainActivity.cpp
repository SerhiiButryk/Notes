#include "com_example_client_app_MainActivity.h"

#include <string>
#include <utility>

#include "app/core/login_action_sender.h"
#include "storage/file_system.h"
#include "utils/jstring.h"

using namespace APP;
using namespace MYLIB;

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_example_notes_test_NotesViewActivity_initFileSystem
  (JNIEnv* env, jobject obj, jstring jstr)
{
    JString filePath(env, jstr);

    FileSystem::getInstance()->initFilePath(filePath);

    jclass clz = env->GetObjectClass(obj);

    JavaVM* javaVm;
    env->GetJavaVM(&javaVm);

    jmethodID mID = env->GetMethodID(clz, "onAuthorization", "()V");

    JNIWrapper callback(javaVm, obj, mID);

    LoginActionSender::getInstance()->addAuthorizeCallback(std::move(callback));
}

#ifdef __cplusplus
}
#endif
