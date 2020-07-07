#include <openssl/ossl_typ.h>

#include "operations.h"
#include "../common/log.h"

namespace MYLIB
{

    bool privateDecrypt(int flen, unsigned char* from, unsigned char* to, RSA* key, int padding) {

        // Return the size of the recovered plaintext
        // 0 - means the palintext is empty
        // -1 - means error
        int rez = RSA_private_decrypt(flen, from, to, key, padding);

        if (rez == 0) 
        {
            LOG_INFO("privateDecrypt", "cyphertext is empty");
        }

        if (rez == -1) 
        {
            LOG_ERROR("privateDecrypt", "failed to decrypt cyphertext");
            return false;
        }

        LOG_INFO("privateDecrypt cyphertext SIZE", rez);
        return true;
    }

    bool publicEncrypt(int flen, unsigned char* from, unsigned char* to, RSA* key, int padding) 
    {
        // Return the size of the encrypted cyphertext
        // 0 - means the palintext is empty
        // -1 - means error
        int rez = RSA_public_encrypt(flen, from, to, key, padding);

        if (rez == 0) 
        {
            LOG_INFO("publicEncrypt", "plaintext is empty");
        }

        if (rez == -1) 
        {
            LOG_ERROR("publicEncrypt", "failed to encrypt plaintext");
            return false;
        }

        LOG_INFO("publicEncrypt cyphertext SIZE", rez);
        return true;
    }


    bool encryptSymmetric(unsigned char *plaintext, int plaintext_len, unsigned char *key,
                unsigned char *iv, unsigned char *ciphertext, const EVP_CIPHER* cypher, int& ciphertext_len) 
    {
        LOG_INFO("encryptSymmetric plaintext SIZE ", plaintext_len);

        EVP_CIPHER_CTX *ctx = nullptr;
        int len = 0;

        // Initialize the context 
        if(!(ctx = EVP_CIPHER_CTX_new())) 
        {
            LOG_ERROR("encryptSymmetric","failed to init contaxt");
            EVP_CIPHER_CTX_free(ctx);
            return false;
        } 

        //   Initialise the encryption operation. 
        //   IMPORTANT - ensure you use a key and IV size appropriate for your cipher
          
        //   For example we can use 256 bit AES (i.e. a 256 bit key). The
        //   IV size for *most* modes is the same as the block size. For AES this
        //   is 128 bits 
          
        //   If impl is NULL then the default implementation is used.
        if( !EVP_EncryptInit_ex(ctx, cypher, NULL, key, iv)) 
        {
            LOG_ERROR("encryptSymmetric","failed to init encrypt operation");
            EVP_CIPHER_CTX_free(ctx);
            return false;
        }

        //  Provide the message to be encrypted, and obtain the encrypted output.
        //  EVP_EncryptUpdate can be called multiple times if necessary
        if( !EVP_EncryptUpdate(ctx, ciphertext, &len, plaintext, plaintext_len)) 
        {
            LOG_ERROR("encryptSymmetric","failed to init encrypt plaintext");
            EVP_CIPHER_CTX_free(ctx);
            return false;
        }

        ciphertext_len = len;

        // Finalise the encryption. Further ciphertext bytes may be written at this stage
        if( !EVP_EncryptFinal_ex(ctx, ciphertext + len, &len)) 
        {
            LOG_ERROR("encryptSymmetric","failed to finalize encrypt plaintext");
            EVP_CIPHER_CTX_free(ctx);
            return false;
        }

        ciphertext_len += len;

        EVP_CIPHER_CTX_free(ctx);
        LOG_INFO("encryptSymmetric ciphertext SIZE ", ciphertext_len);
        return true;
    }

    bool decryptSymmetric(unsigned char *ciphertext, int ciphertext_len, unsigned char *key,
                unsigned char *iv, unsigned char *plaintext, const EVP_CIPHER* cypher, int& plaintext_len) 
    {
        EVP_CIPHER_CTX *ctx;
        int len;

        // Initialize the context
        if(!(ctx = EVP_CIPHER_CTX_new())) 
        {
            LOG_ERROR("decryptSymmetric","failed to init contaxt");
            EVP_CIPHER_CTX_free(ctx);
            return false;
        }

        //   Initialise the encryption operation. 
        //   IMPORTANT - ensure you use a key and IV size appropriate for your cipher
          
        //   For example we can use 256 bit AES (i.e. a 256 bit key). The
        //   IV size for *most* modes is the same as the block size. For AES this
        //   is 128 bits 
          
        //   If impl is NULL then the default implementation is used.
        if( !EVP_DecryptInit_ex(ctx, cypher, NULL, key, iv)) 
        {
            LOG_ERROR("decryptSymmetric","failed to init encrypt operation");
            EVP_CIPHER_CTX_free(ctx);
            return false;
        }

        //  Provide the message to be encrypted, and obtain the encrypted output.
        //  EVP_EncryptUpdate can be called multiple times if necessary
        if( !EVP_DecryptUpdate(ctx, plaintext, &len, ciphertext, ciphertext_len))
        {
            LOG_ERROR("decryptSymmetric","failed to init dencrypt plaintext");
            EVP_CIPHER_CTX_free(ctx);
            return false;
        }

        plaintext_len = len;

        // Finalise the encryption. Further ciphertext bytes may be written at this stage
        if( !EVP_DecryptFinal_ex(ctx, plaintext + len, &len)) 
        {
            LOG_ERROR("decryptSymmetric","failed to finalize dencrypt plaintext");
            EVP_CIPHER_CTX_free(ctx);
            return false;
        } 

        plaintext_len += len;

        EVP_CIPHER_CTX_free(ctx);
        LOG_INFO("decryptSymmetric plaintext SIZE ", plaintext_len);
        return true;
    }

}