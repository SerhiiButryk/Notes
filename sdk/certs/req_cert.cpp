#include <openssl/pem.h>
#include <string>
#include "req_cert.h"
#include "../common/log.h"

namespace MYLIB 
{
    ReqCert::ReqCert() : _req(X509_REQ_new(), X509_REQ_free) {}

    void ReqCert::setNameAttributes(const char *country, const char *province, const char *city,
        const char *organization, const char *common) {
        
        _name.setSubjectNameRequest(_req.get())
             .setCountry(country)
             .setState(province)
             .setLocality(city)
             .setOrganization(organization)
             .setCommonName(common)
             .build();
    }

    void ReqCert::makeReq(RSA *rsa) {
        EVP_PKEY* pkey = EVP_PKEY_new();
        EVP_PKEY_assign_RSA(pkey, rsa);

        int success = X509_REQ_set_pubkey(_req.get(), pkey);
        
        if (success != 1) {
            LOG_ERROR("ReqCert::makeReq", "Public key is not set");
        }

        success = X509_REQ_sign(_req.get(), pkey, EVP_sha256());    // return _req->signature->length

        if (success <= 0) {
            LOG_ERROR("ReqCert::makeReq", "Failed to sign");    
        } 
    }

    X509_REQ* ReqCert::getCertificate() const {
        return _req.get();
    }

    void ReqCert::setVersion(int version) {
        int success = X509_REQ_set_version(_req.get(), version);
        
        if (success <= 0) {
            LOG_ERROR("ReqCert::setVersion", "Failed to set version");    
        }
    }

    void ReqCert::save(const char *file_name) {
        FILE* f = fopen(file_name, "wb");

        if (f == 0) {
            LOG_ERROR("ReqCert::save", "Failed to open file");
            fclose(f);
            return;
        }

        int success = PEM_write_X509_REQ(f, _req.get());
        
        if (success != 1) {
            LOG_ERROR("ReqCert::save", "Failed to save X509 REQ");
        }

        fclose(f);
    }
}