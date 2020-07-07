#ifndef _TO_PK12_
#define _TO_PK12_

#include <openssl/pkcs12.h>
#include <openssl/x509.h>
#include <memory>
#include <openssl/pem.h>
#include <openssl/x509v3.h>
#include <openssl/bio.h>
#include "../common/log.h"

namespace MYLIB 
{
    class Utils 
    {
        private:

            Utils();

        public:

            static PKCS12* convertToP12(RSA *client_rsa_key, const char *password, const char *name,
                X509 *crt, STACK_OF(X509) *cacert_stack = 0);

            static void saveFileP12(const char *file_name, const PKCS12* pk12);

            static X509* readX509(const char* file_name);

            /** Read from memory buffer */
            static X509* readRawX509(const unsigned char* data, long length);

            // Cert managment
            static std::string getCertificateIssuer(const X509* crt);

            static std::string getCertificateSubject(const X509* crt);

            static int getVersion(const X509* crt);
    };
}

#endif