#ifndef _OPERATIONS_H_
#define _OPERATIONS_H_

#include <openssl/rsa.h>
#include <openssl/evp.h>

namespace MYLIB 
{

    /*
     * Dencrypts the flen bytes at from using the 
     * private key rsa and stores the ciphertext in to. 
    */
    bool privateDecrypt(int flen, unsigned char* from, unsigned char* to, RSA* key, int padding);

    /*
     * Encrypts the flen bytes at from (usually a session key) using the 
     * public key rsa and stores the ciphertext in to. 
     * 
     * to must point to RSA_size(rsa) bytes of memory.
     * 
     * RSA_NO_PADDING can be used for padding.
     * 
     * Padding is the padding mode that was used to encrypt the data
     * and is fixed to padding mode.
    */
    bool publicEncrypt(int flen, unsigned char* from, unsigned char* to, RSA* key, int padding);

    bool encryptSymmetric(unsigned char *plaintext, int plaintext_len, unsigned char *key,
                          unsigned char *iv, unsigned char *ciphertext, 
                          const EVP_CIPHER* cypher, int& ciphertext_len);
    
    bool decryptSymmetric(unsigned char *ciphertext, int ciphertext_len, unsigned char *key,
                          unsigned char *iv, unsigned char *plaintext, 
                          const EVP_CIPHER* cypher, int& plaintext_len);
}

#endif 
