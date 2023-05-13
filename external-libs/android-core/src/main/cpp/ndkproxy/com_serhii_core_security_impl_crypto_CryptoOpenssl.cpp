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
    Log::Info("JNI", " %s IN", __FUNCTION__ );

    JString plaintext(env, jplaintext);
    JString key(env, jkey);
    JString iv(env, jiv);

    unsigned char _key[KEY_SIZE] = {0};
    unsigned char _iv[BLOCK_SIZE] = {0};

    strncpy((char*) _key, key, KEY_SIZE);
    strncpy((char*) _iv, iv, BLOCK_SIZE);

    std::string cypherText;

    int result = CryptoUtils::AESEncrypt(_key, _iv, plaintext, cypherText);

    if (result != 1) {
        Log::Error("JNI", "%s failed to encrypt OUT", __FUNCTION__ );
        return env->NewStringUTF("");
    }

    std::string encodedText = Base64::encode((unsigned char*) cypherText.c_str(), cypherText.length());

    Log::Info("JNI", "%s OUT", __FUNCTION__ );

    return env->NewStringUTF(encodedText.c_str());
}

JNIEXPORT jstring JNICALL Java_com_serhii_core_security_impl_crypto_Openssl__1decryptSymmetric(JNIEnv *env, jobject thiz, jstring jcypher_text, jstring jkey, jstring jiv)
{
    Log::Info("JNI", "%s IN", __FUNCTION__ );

    JString cypherText(env, jcypher_text);
    JString key(env, jkey);
    JString iv(env, jiv);

    std::string decodedText = Base64::decode(cypherText);
    std::string decodedIV = Base64::decode(key);
    std::string decodedKEY = Base64::decode(iv);

    std::string plainText;
    std::string cypherTextString = cypherText;

    int result = CryptoUtils::AESDecrypt((unsigned char*) decodedKEY.c_str(), (unsigned char*) decodedIV.c_str(), plainText, cypherTextString);

    if (result != 1) {
        Log::Error("JNI", "%s failed to decrypt OUT", __FUNCTION__ );
        return env->NewStringUTF("");
    }

    Log::Info("JNI", "%s OUT", __FUNCTION__ );

    return env->NewStringUTF(plainText.c_str());
}

#ifdef __cplusplus
}
#endif
