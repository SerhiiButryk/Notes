#ifndef _REQ_SIGNER_H_
#define _REQ_SIGNER_H_

#include <openssl/x509.h>
#include <memory>

namespace MYLIB 
{

    class SignReqCert 
    {
        public:

            SignReqCert();
            ~SignReqCert() = default;

            void setVersion(int version);
            void setSerialNumber(int serial_number);
            void makeSign(EVP_PKEY *pkey, X509 *ca_cert, X509_REQ *client_req, const EVP_MD *method,
                        long elapse_time_before, long elapse_time_after);
            void save(const char *file_name);
            X509* getCertificate() const;

        private:

            std::unique_ptr<X509, decltype(&X509_free)> _crt;
    };

}

#endif