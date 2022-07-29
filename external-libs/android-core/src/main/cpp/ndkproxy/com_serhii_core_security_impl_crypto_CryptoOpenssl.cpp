#include "com_serhii_core_security_impl_crypto_CryptoOpenssl.h"

#include <string>
#include <algorithm>

#include "crypto/crypto_utils.h"
#include "utils/log.h"
#include "utils/base64.h"
#include "utils/jstring.h"

#include <memory>

using namespace MYLIB;

// TODO: Check this later if this can be removed
int MAX_LENGTH_BOUNDARY = 100;

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jstring JNICALL Java_com_serhii_core_security_impl_crypto_CryptoOpenssl__1encryptSymmetric(JNIEnv *env,
    jobject thiz, jstring jplaintext, jstring jkey, jstring jiv)
{
    Log::Info("JNI", " %s IN", __FUNCTION__ );

    JString plaintext(env, jplaintext);
    JString key(env, jkey);
    JString iv(env, jiv);

    /* Need to make that this buffer is long enough for ciphertext */
    auto* buffer = new unsigned char[plaintext.getSize() + MAX_LENGTH_BOUNDARY];
    int bufferLen;

    /* Encrypt the plaintext */
    bufferLen = CryptoUtils::encryptSymmetric(plaintext, plaintext.getSize(), key, iv, buffer);

    /* Add a NULL terminator */
    buffer[bufferLen] = '\0';

    std::string encryptedText = Base64::encode(buffer, bufferLen);

    // Delete allocated memory
    delete[] buffer;

    Log::Info("JNI", " %s OUT", __FUNCTION__ );

    return env->NewStringUTF(encryptedText.c_str());
}

JNIEXPORT jstring JNICALL Java_com_serhii_core_security_impl_crypto_CryptoOpenssl__1decryptSymmetric(JNIEnv *env,
    jobject thiz, jstring jcypher_text, jstring jkey, jstring jiv)
{
    Log::Info("JNI", " %s IN", __FUNCTION__ );

    JString cypherText(env, jcypher_text);
    JString key(env, jkey);
    JString iv(env, jiv);

    std::string inputText = Base64::decode(cypherText);

    auto* buffer = new unsigned char[inputText.size()];
    int bufferLen;

    /* Decrypt the ciphertext */
    bufferLen = CryptoUtils::decryptSymmetric((unsigned char *) inputText.c_str(), inputText.length(), key, iv, buffer);

    /* Add a NULL terminator. We are expecting printable text */
    buffer[bufferLen] = '\0';

    std::string plainText((char*) buffer, bufferLen);

    // Delete allocated memory
    delete[] buffer;

    Log::Info("JNI", " %s OUT", __FUNCTION__ );

    return env->NewStringUTF(plainText.c_str());
}

#ifdef __cplusplus
}
#endif
