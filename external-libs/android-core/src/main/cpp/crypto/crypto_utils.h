#pragma once

#include <openssl/rsa.h>
#include <openssl/evp.h>
#include <string>
#include <jni.h>

namespace MYLIB
{
    static const unsigned int KEY_SIZE = 32;
    static const unsigned int BLOCK_SIZE = 16;

    class CryptoUtils
    {
    public:
        JNIEXPORT static int AESEncrypt(const unsigned char* key, const unsigned char* iv, const std::string& ptext, std::string& ctext);
        JNIEXPORT static int AESDecrypt(const unsigned char* key, const unsigned char* iv, const std::string& ctext, std::string& rtext);

        JNIEXPORT static int genKey(unsigned char *key, unsigned char *iv);
    };

}
