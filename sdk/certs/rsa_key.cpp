#include <openssl/rsa.h>
#include <string>
#include <string.h>
#include <openssl/evp.h>
#include <openssl/pem.h>
#include "rsa_key.h"
#include "../common/log.h"

namespace MYLIB 
{

    RSAKey::RSAKey() : _rsa(RSA_new(), RSA_free),
       _key_exponent(RSA_F4), _key_length(4096) {}

    RSAKey::RSAKey(unsigned int key_length, unsigned int key_exponent) :
        _rsa(RSA_new(), RSA_free) {
        this->_key_length = key_length;
        this->_key_exponent = key_exponent;
    }

    void RSAKey::makeKey() {
        BIGNUM* bn_exp = BN_new();
        BN_set_word(bn_exp, _key_exponent);
        RSA_generate_key_ex(_rsa.get(), _key_length, bn_exp, 0);
        
        if (!_rsa) {
            LOG_ERROR("RSAKey::makeKey", "Rsa is null");
        }

        BN_free(bn_exp);
    }

    void RSAKey::savePrivateKey(const char *file_full_name, const char *password, const EVP_CIPHER *cipher) {
        int size = strlen(password);

        FILE* f = fopen(file_full_name, "wb");

        if (!f) {
            LOG_ERROR("RSAKey::savePrivateKey", "Failed to open file");
            fclose(f);
            return;
        }

        int success = PEM_write_RSAPrivateKey(f, _rsa.get(), cipher, (unsigned char*)password, size, 0, 0);
        
        if (success != 1) {
            LOG_ERROR("RSAKey::savePrivateKey", "Failed to save private key");
        }

        fclose(f);
    }

    RSA* RSAKey::getKey() const {
        return _rsa.get();
    }

    EVP_PKEY* RSAKey::getEVP() {
        EVP_PKEY* pkey = EVP_PKEY_new();
        EVP_PKEY_assign_RSA(pkey, _rsa.get());
        return pkey;
    }

    void RSAKey::setKeyLength(unsigned key_len) {
        _key_length = key_len;
    }

    void RSAKey::savePublicKey(const char *file_full_name) {
        FILE* f = fopen(file_full_name, "wb");

         if (!f) {
            LOG_ERROR("RSAKey::savePublicKey", "Failed to open file");
            fclose(f);
            return;
        }

        int success = PEM_write_RSAPublicKey(f, _rsa.get());

        if (success != 1) {
            LOG_ERROR("RSAKey::savePublicKey", "Failed to save public key");
        }

        fclose(f);
    }

    void RSAKey::setExponentLength(unsigned exponent_len) {
        _key_exponent = exponent_len;
    }
}