#pragma once

#include <openssl/rsa.h>
#include <openssl/evp.h>
#include <string>

namespace MYLIB
{
    static const unsigned int KEY_SIZE = 32;
    static const unsigned int BLOCK_SIZE = 16;

    class CryptoUtils
    {
    public:
        static int AESEncrypt(const unsigned char* key, const unsigned char* iv, const std::string& ptext, std::string& ctext);
        static int AESDecrypt(const unsigned char* key, const unsigned char* iv, const std::string& ctext, std::string& rtext);

        static int genKey(unsigned char *key, unsigned char *iv);
    };

}
