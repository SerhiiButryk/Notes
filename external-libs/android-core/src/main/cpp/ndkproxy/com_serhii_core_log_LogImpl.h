#include <jni.h>

#ifndef _Included_com_example_core_common_log_impl_LogImpl
#define _Included_com_example_core_common_log_impl_LogImpl

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_serhii_core_log_LogImpl__1setTag(JNIEnv *, jobject, jstring);
JNIEXPORT void JNICALL Java_com_serhii_core_log_LogImpl__1setDetailLog(JNIEnv *env, jobject thiz, jboolean enable);
JNIEXPORT jboolean JNICALL Java_com_serhii_core_log_LogImpl__1isDetailLogEnabled(JNIEnv *env, jobject thiz);
JNIEXPORT void JNICALL Java_com_serhii_core_log_LogImpl__1enableDetailLogIfDebug(JNIEnv *env, jobject thiz);

#ifdef __cplusplus
}
#endif

#endif
