#include <jni.h>

#ifndef _Included_com_serhii_core_security_KeyMaster
#define _Included_com_serhii_core_security_KeyMaster

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_serhii_core_security_impl_KeyMaster__1save(JNIEnv *env, jobject thiz, jstring value);
JNIEXPORT void JNICALL Java_com_serhii_core_security_impl_KeyMaster__1save2(JNIEnv *env, jobject thiz, jstring value);
JNIEXPORT void JNICALL Java_com_serhii_core_security_impl_KeyMaster__1save3(JNIEnv *env, jobject thiz, jstring value);
JNIEXPORT void JNICALL Java_com_serhii_core_security_impl_KeyMaster__1save4(JNIEnv *env, jobject thiz, jstring value);
JNIEXPORT void JNICALL Java_com_serhii_core_security_impl_KeyMaster__1save5(JNIEnv *env, jobject thiz, jstring value);

JNIEXPORT jstring JNICALL Java_com_serhii_core_security_impl_KeyMaster__1get(JNIEnv *env, jobject thiz);
JNIEXPORT jstring JNICALL Java_com_serhii_core_security_impl_KeyMaster__1get2(JNIEnv *env, jobject thiz);
JNIEXPORT jstring JNICALL Java_com_serhii_core_security_impl_KeyMaster__1get3(JNIEnv *env, jobject thiz);
JNIEXPORT jstring JNICALL Java_com_serhii_core_security_impl_KeyMaster__1get4(JNIEnv *env, jobject thiz);
JNIEXPORT jstring JNICALL Java_com_serhii_core_security_impl_KeyMaster__1get5(JNIEnv *env, jobject thiz);

JNIEXPORT jstring JNICALL Java_com_serhii_core_security_impl_KeyMaster__1getUnlockKey(JNIEnv *, jobject);
JNIEXPORT void JNICALL Java_com_serhii_core_security_impl_KeyMaster__1setUnlockKey(JNIEnv *env, jobject thiz, jstring unlock_key);

#ifdef __cplusplus
}
#endif

#endif
