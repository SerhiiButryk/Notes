#include "com_serhii_core_FileSystem.h"

#include "utils/log.h"
#include "utils/jstring.h"
#include "storage/file_system.h"

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_serhii_core_FileSystem__1setSystemFilePath(JNIEnv *env, jobject thiz, jstring path)
{
    MYLIB::Log::Info("JNI", " %s IN", __FUNCTION__ );

    MYLIB::JString filePath(env, path);

    MYLIB::FileSystem::getInstance()->initFilePath(filePath);

    MYLIB::Log::Info("JNI", " %s OUT", __FUNCTION__ );
}

#ifdef __cplusplus
}
#endif