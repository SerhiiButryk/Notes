#include "crypto_utils.h"

#include <openssl/conf.h>
#include <openssl/evp.h>
#include <openssl/err.h>
#include <openssl/rand.h>
#include <string>

#include "utils/log.h"

namespace {
    const std::string TAG = "CryptoUtils";
}

namespace MYLIB {

    int CryptoUtils::AESEncrypt(const unsigned char* key, const unsigned char* iv, const std::string& ptext, std::string& ctext)
    {
        using EVP_CIPHER_CTX_free_ptr = std::unique_ptr<EVP_CIPHER_CTX, decltype(&::EVP_CIPHER_CTX_free)>;

        EVP_CIPHER_CTX_free_ptr ctx(EVP_CIPHER_CTX_new(), ::EVP_CIPHER_CTX_free);
        int rc = EVP_EncryptInit_ex(ctx.get(), EVP_aes_256_cbc(), NULL, key, iv);
        if (rc != 1) {
            Log::Error(TAG, "AESEncrypt() failed to init context");
            return 0;
        }

        // Recovered text expands upto BLOCK_SIZE
        ctext.resize(ptext.size()+BLOCK_SIZE);
        int out_len1 = (int)ctext.size();

        rc = EVP_EncryptUpdate(ctx.get(), (unsigned char*)&ctext[0], &out_len1, (const unsigned char*)&ptext[0], (int)ptext.size());
        if (rc != 1) {
            Log::Error(TAG, "AESEncrypt() failed update");
            return 0;
        }

        int out_len2 = (int)ctext.size() - out_len1;
        rc = EVP_EncryptFinal_ex(ctx.get(), (unsigned char*)&ctext[0]+out_len1, &out_len2);
        if (rc != 1) {
            Log::Error(TAG, "AESEncrypt() failed final");
            return 0;
        }

        // Set cipher text size now that we know it
        ctext.resize(out_len1 + out_len2);
        return 1;
    }
    
    int CryptoUtils::AESDecrypt(const unsigned char* key, const unsigned char* iv, const std::string& ctext, std::string& rtext)
    {
        using EVP_CIPHER_CTX_free_ptr = std::unique_ptr<EVP_CIPHER_CTX, decltype(&::EVP_CIPHER_CTX_free)>;

        EVP_CIPHER_CTX_free_ptr ctx(EVP_CIPHER_CTX_new(), ::EVP_CIPHER_CTX_free);
        int rc = EVP_DecryptInit_ex(ctx.get(), EVP_aes_256_cbc(), NULL, key, iv);
        if (rc != 1) {
            Log::Error(TAG, "AESDecrypt() failed to init context");
            return 0;
        }
        // Recovered text contracts upto BLOCK_SIZE
        rtext.resize(ctext.size());
        int out_len1 = (int)rtext.size();

        rc = EVP_DecryptUpdate(ctx.get(), (unsigned char*)&rtext[0], &out_len1, (const unsigned char*)&ctext[0], (int)ctext.size());
        if (rc != 1) {
            Log::Error(TAG, "AESDecrypt() failed to update");
            return 0;
        }

        int out_len2 = (int)rtext.size() - out_len1;
        rc = EVP_DecryptFinal_ex(ctx.get(), (unsigned char*)&rtext[0]+out_len1, &out_len2);
        if (rc != 1) {
            Log::Error(TAG, "AESDecrypt() failed to final");
            return 0;
        }
        // Set recovered text size now that we know it
        rtext.resize(out_len1 + out_len2);
        return 1;
    }

    int CryptoUtils:: genKey(unsigned char *key, unsigned char *iv)
    {
        int rc = RAND_bytes(key, KEY_SIZE);
        if (rc != 1) {
            Log::Error(TAG, "genKey() failed to gen key");
            return rc;
        }

        rc = RAND_bytes(iv, BLOCK_SIZE);
        if (rc != 1) {
            Log::Error(TAG, "genKey() failed to gen iv");
            return rc;
        }

        Log::Info(TAG, "genKey() success");
        return rc;
    }

}
