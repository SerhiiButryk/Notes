#ifndef _CRYPTO_CA_CERT_H_
#define _CRYPTO_CA_CERT_H_

#include <openssl/x509.h>
#include <memory>
#include "xname.h"

namespace MYLIB 
{
    class CACert 
    {
        public:

            CACert();
            ~CACert() = default;

            void setNameAttributes(const char *country_code, const char *company, const char *host);
            void setSerialNumber(unsigned serial_number);
            void setElapseTime(long elapse_time_before, long elapse_time_after);
            void makeSelfSign(const EVP_MD *method, EVP_PKEY* pkey);
            void save(const char *file_name);
            X509* getCertificate() const;

        private:

            std::unique_ptr<X509, decltype(&X509_free)> _crt;
            XName _name;
    };
}

#endif
