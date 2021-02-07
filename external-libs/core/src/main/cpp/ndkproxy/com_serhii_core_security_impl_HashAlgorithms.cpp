#include "com_serhii_core_security_impl_HashAlgorithms.h"

#include "crypto/hash.h"
#include "utils/jstring.h"

using namespace MYLIB;

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jstring JNICALL Java_com_serhii_core_security_impl_hash_HashAlgorithms_hashMD5(JNIEnv* env, jobject, jstring message)
  {
     JString strMessage(env, message);

     return env->NewStringUTF(makeHashMD5(strMessage).c_str());
  }

#ifdef __cplusplus
}
#endif