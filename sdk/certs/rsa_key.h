#ifndef _CAREQUEST_H_
#define _CAREQUEST_H_

#include <openssl/ossl_typ.h>
#include <memory>

namespace MYLIB 
{
    class RSAKey 
    {
        public:

            RSAKey();
            RSAKey(unsigned int key_length, unsigned int key_exponent);
            ~RSAKey() = default;

            void makeKey();
            void savePrivateKey(const char *file_full_name, const char *password, const EVP_CIPHER *cipher);
            void savePublicKey(const char *file_full_name);
            RSA* getKey() const;
            EVP_PKEY* getEVP() ;
            void setKeyLength(unsigned key_len);
            void setExponentLength(unsigned exponent_len);

        private:

            std::unique_ptr<RSA, decltype(&RSA_free)>  _rsa;
            unsigned _key_length;
            unsigned _key_exponent;
    };
}

#endif