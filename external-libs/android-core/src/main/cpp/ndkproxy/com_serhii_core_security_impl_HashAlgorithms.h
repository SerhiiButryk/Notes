#include <jni.h>

#ifndef _Included_com_example_core_security_impl_HashAlgorithms
#define _Included_com_example_core_security_impl_HashAlgorithms
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_example_core_security_impl_HashAlgorithms
 * Method:    _makeHashMD5
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_serhii_core_security_impl_hash_HashAlgorithms_hashMD5(JNIEnv *, jobject, jstring);

#ifdef __cplusplus
}
#endif
#endif
