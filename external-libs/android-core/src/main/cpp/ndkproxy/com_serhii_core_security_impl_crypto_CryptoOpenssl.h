#include <jni.h>

#ifndef _Included_com_example_core_security_impl_HashAlgorithms
#define _Included_com_example_core_security_impl_HashAlgorithms
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jstring JNICALL Java_com_serhii_core_security_impl_crypto_Openssl__1encryptSymmetric(JNIEnv *env,
                                                                                               jobject thiz, jstring jplaintext, jstring jkey, jstring jiv);

JNIEXPORT jstring JNICALL Java_com_serhii_core_security_impl_crypto_Openssl__1decryptSymmetric(JNIEnv *env,
                                                                                               jobject thiz, jstring jcypher_text, jstring jkey, jstring jiv);

#ifdef __cplusplus
}
#endif
#endif
