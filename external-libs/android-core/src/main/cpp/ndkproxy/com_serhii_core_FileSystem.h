#include <jni.h>

#ifndef _Included_com_serhii_core_FileSystem
#define _Included_com_serhii_core_FileSystem

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_serhii_core_FileSystem__1setSystemFilePath(JNIEnv *env, jobject thiz, jstring path);

#ifdef __cplusplus
}
#endif

#endif