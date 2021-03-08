#include "com_example_client_app_NotesViewActivity.h"

#include <string>
#include <utility>

#include "app/logic/app_action.h"
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

JNIEXPORT void JNICALL Java_com_serhii_apps_notes_activities_NotesViewActivity_initNativeConfigs
  (JNIEnv* env, jobject obj, jstring jstr)
{
    JString filePath(env, jstr);

    FileSystem::getInstance()->initFilePath(filePath);

    jclass clz = env->GetObjectClass(obj);

    JavaVM* javaVm;
    env->GetJavaVM(&javaVm);

    jmethodID id = env->GetMethodID(clz, "onUserAuthorized", "()V");

    JNIWrapper callback(javaVm, obj, id);

    AppAction::getInstance()->setAuthorizeCallback(std::move(callback));
}

#ifdef __cplusplus
}
#endif
