#include "crypto_utils.h"

#include <openssl/conf.h>
#include <openssl/evp.h>
#include <openssl/err.h>
#include <string>

#include "utils/log.h"

namespace {
    const std::string TAG = "CryptoUtils";
}

namespace MYLIB {

    int CryptoUtils::encryptSymmetric(unsigned char *plaintext, int plaintext_len, unsigned char *key,
                                      unsigned char *iv, unsigned char *ciphertext)
    {
        EVP_CIPHER_CTX *ctx;

        int len;

        int ciphertext_len;

        /* Create and initialise the context */
        if(!(ctx = EVP_CIPHER_CTX_new()))
        {
            Log::Error(TAG, "encrypt() failed to init contaxt");
            return 0;
        }

        /*
         * Initialise the encryption operation. IMPORTANT - ensure you use a key
         * and IV size appropriate for your cipher
         * In this example we are using 256 bit AES (i.e. a 256 bit key). The
         * IV size for *most* modes is the same as the block size. For AES this
         * is 128 bits
         */
        if(1 != EVP_EncryptInit_ex(ctx, EVP_aes_256_cbc(), NULL, key, iv))
        {
            Log::Error(TAG, "encrypt() failed to init evp");
            EVP_CIPHER_CTX_free(ctx);
            return 0;
        }

        /*
         * Provide the message to be encrypted, and obtain the encrypted output.
         * EVP_EncryptUpdate can be called multiple times if necessary
         */
        if(1 != EVP_EncryptUpdate(ctx, ciphertext, &len, plaintext, plaintext_len))
        {
            Log::Error(TAG, "encrypt() failed to update evp");
            EVP_CIPHER_CTX_free(ctx);
            return 0;
        }

        ciphertext_len = len;

        /*
         * Finalise the encryption. Further ciphertext bytes may be written at
         * this stage.
         */
        if(1 != EVP_EncryptFinal_ex(ctx, ciphertext + len, &len))
        {
            Log::Error(TAG, "encrypt() failed to final evp");
            EVP_CIPHER_CTX_free(ctx);
            return 0;
        }

        ciphertext_len += len;

        /* Clean up */
        EVP_CIPHER_CTX_free(ctx);

        return ciphertext_len;
    }

    int CryptoUtils::decryptSymmetric(unsigned char *ciphertext, int ciphertext_len, unsigned char *key,
                                      unsigned char *iv, unsigned char *plaintext)
    {
        EVP_CIPHER_CTX *ctx;

        int len;

        int plaintext_len;

        /* Create and initialise the context */
        if(!(ctx = EVP_CIPHER_CTX_new()))
        {
            Log::Error(TAG, "decrypt() failed to init contaxt");
            return 0;
        }

        /*
         * Initialise the decryption operation. IMPORTANT - ensure you use a key
         * and IV size appropriate for your cipher
         * In this example we are using 256 bit AES (i.e. a 256 bit key). The
         * IV size for *most* modes is the same as the block size. For AES this
         * is 128 bits
         */
        if(1 != EVP_DecryptInit_ex(ctx, EVP_aes_256_cbc(), NULL, key, iv))
        {
            Log::Error(TAG, "decrypt() failed to init evp");
            EVP_CIPHER_CTX_free(ctx);
            return 0;
        }

        /*
         * Provide the message to be decrypted, and obtain the plaintext output.
         * EVP_DecryptUpdate can be called multiple times if necessary.
         */
        if(1 != EVP_DecryptUpdate(ctx, plaintext, &len, ciphertext, ciphertext_len))
        {
            Log::Error(TAG, "decrypt() failed to update evp");
            EVP_CIPHER_CTX_free(ctx);
            return 0;
        }

        plaintext_len = len;

        /*
         * Finalise the decryption. Further plaintext bytes may be written at
         * this stage.
         */
        if(1 != EVP_DecryptFinal_ex(ctx, plaintext + len, &len))
        {
            Log::Error(TAG, "decrypt() failed to final evp");
            EVP_CIPHER_CTX_free(ctx);
            return 0;
        }

        plaintext_len += len;

        /* Clean up */
        EVP_CIPHER_CTX_free(ctx);

        return plaintext_len;
    }

}
