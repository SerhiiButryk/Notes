#pragma once

#include <openssl/rsa.h>
#include <openssl/evp.h>

namespace MYLIB
{
    class CryptoUtils
    {
    public:
        static bool encryptSymmetric(unsigned char *plaintext, int plaintext_len, unsigned char *key,
                              unsigned char *iv, unsigned char *ciphertext,
                              const EVP_CIPHER* cypher, int& ciphertext_len);

        static bool decryptSymmetric(unsigned char *ciphertext, int ciphertext_len, unsigned char *key,
                              unsigned char *iv, unsigned char *plaintext,
                              const EVP_CIPHER* cypher, int& plaintext_len);

        static int encrypt(unsigned char *plaintext, int plaintext_len, unsigned char *key,
                    unsigned char *iv, unsigned char *ciphertext);

        static int decrypt(unsigned char *ciphertext, int ciphertext_len, unsigned char *key,
                    unsigned char *iv, unsigned char *plaintext);

    };

}
