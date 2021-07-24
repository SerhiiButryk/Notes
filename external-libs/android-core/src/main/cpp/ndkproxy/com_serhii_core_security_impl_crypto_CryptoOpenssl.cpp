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

JNIEXPORT jstring JNICALL Java_com_serhii_core_security_impl_crypto_CryptoOpenssl__1encryptSymmetric(JNIEnv *env,
    jobject thiz, jstring jplaintext, jstring jkey, jstring jiv)
{
    Log::Info("JNI", " %s IN", __FUNCTION__ );

    JString plaintext(env, jplaintext);
    JString key(env, jkey);
    JString iv(env, jiv);

    std::unique_ptr<unsigned char> ciphertext(new unsigned char[plaintext.getSize()]{ 0 } );

    /* Encrypt the plaintext */
    size_t ciphertext_len = CryptoUtils::encryptSymmetric(plaintext, plaintext.getSize(), key, iv,
                                                          ciphertext.get());

    std::string res = Base64::encode(ciphertext.get(), ciphertext_len);

    // DEBUG ONLY
    //Log::Info("JNI", " %s, Encrypted text: %s  OUT", __FUNCTION__, res.c_str());

    Log::Info("JNI", " %s OUT", __FUNCTION__ );

    return env->NewStringUTF(res.c_str());
}

JNIEXPORT jstring JNICALL Java_com_serhii_core_security_impl_crypto_CryptoOpenssl__1decryptSymmetric(JNIEnv *env,
    jobject thiz, jstring jcypher_text, jstring jkey, jstring jiv)
{
    Log::Info("JNI", " %s IN", __FUNCTION__ );

    JString cypherText(env, jcypher_text);
    JString key(env, jkey);
    JString iv(env, jiv);

    // DEBUG ONLY
    // Log::Info("JNI", "Encrypted text: %s", (const char*) cypherText);

    std::string rez = Base64::decode(cypherText);

    std::unique_ptr<unsigned char> decryptedtext(new unsigned char[cypherText.getSize()]{ 0 } );

    int decryptedtext_len;

    /* Decrypt the ciphertext */
    decryptedtext_len = CryptoUtils::decryptSymmetric((unsigned char *) rez.c_str(), rez.length(),
                                                      key, iv, decryptedtext.get());

    /* Add a NULL terminator. We are expecting printable text */
    decryptedtext.get()[decryptedtext_len] = '\0';

    std::string rezText((char*) decryptedtext.get(), decryptedtext_len);

    // DEBUG ONLY
    // Log::Info("JNI", " %s, Decrypted text: %s  OUT", __FUNCTION__, decryptedtext);

    Log::Info("JNI", " %s OUT", __FUNCTION__ );

    return env->NewStringUTF(rezText.c_str());
}

#ifdef __cplusplus
}
#endif
