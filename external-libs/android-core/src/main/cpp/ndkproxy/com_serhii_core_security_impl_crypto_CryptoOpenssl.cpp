#include "com_serhii_core_security_impl_crypto_CryptoOpenssl.h"

#include <string>
#include <algorithm>

#include "crypto/crypto_utils.h"
#include "utils/log.h"
#include "utils/base64.h"
#include "utils/jstring.h"

#include <memory>

using namespace MYLIB;

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jstring JNICALL Java_com_serhii_core_security_impl_crypto_Openssl__1encryptSymmetric(JNIEnv *env, jobject thiz, jstring jplaintext, jstring jkey, jstring jiv)
{
    Log::Info("JNI", "%s IN", __FUNCTION__ );

    JString plaintext(env, jplaintext);
    JString key(env, jkey);
    JString iv(env, jiv);

    constexpr static const int key_length = 32;
    constexpr static const int iv_length = 16;

    std::string cipherText;

    CryptoUtils::symmetricEncrypt<key_length, iv_length>(plaintext, cipherText, key, iv);

    Log::Info("JNI", "%s OUT", __FUNCTION__ );

    return env->NewStringUTF(cipherText.c_str());
}

JNIEXPORT jstring JNICALL Java_com_serhii_core_security_impl_crypto_Openssl__1decryptSymmetric(JNIEnv *env, jobject thiz, jstring jcypher_text, jstring jkey, jstring jiv)
{
    Log::Info("JNI", "%s IN", __FUNCTION__ );

    JString cipherText(env, jcypher_text);
    JString key(env, jkey);
    JString iv(env, jiv);

    constexpr static const int key_length = 32;
    constexpr static const int iv_length = 16;

    std::string plainText;

    CryptoUtils::symmetricDecrypt<key_length, iv_length>(cipherText, plainText, key, iv);

    Log::Info("JNI", "%s OUT", __FUNCTION__ );

    return env->NewStringUTF(plainText.c_str());
}

#ifdef __cplusplus
}
#endif
