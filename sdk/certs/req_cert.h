#ifndef _REQUEST_HANDLER_H_
#define _REQUEST_HANDLER_H_

#include <openssl/x509.h>
#include <memory>
#include "xname.h"

namespace MYLIB 
{
    class ReqCert 
    {
        public:

            ReqCert();
            ~ReqCert() = default;

            void setNameAttributes(const char *country, const char *province, const char *city, const char *organization,
                                const char *common);
            void makeReq(RSA *rsa);
            void setVersion(int version);
            void save(const char *file_name);
            X509_REQ* getCertificate() const;

        private:

            std::unique_ptr<X509_REQ, decltype(&X509_REQ_free)>  _req;
            XName _name;
    };
}

#endif