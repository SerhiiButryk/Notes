#pragma once

#include <openssl/rsa.h>
#include <openssl/evp.h>

namespace MYLIB
{
    class CryptoUtils
    {
    public:
        static int encryptSymmetric(unsigned char *plaintext, int plaintext_len, unsigned char *key,
                                    unsigned char *iv, unsigned char *ciphertext);

        static int decryptSymmetric(unsigned char *ciphertext, int ciphertext_len, unsigned char *key,
                                    unsigned char *iv, unsigned char *plaintext);
    };

}
