#include "sign_req_cert.h"
#include "../common/log.h"
#include <openssl/pem.h>

namespace MYLIB 
{

    SignReqCert::SignReqCert() : _crt(X509_new(), X509_free) {}
    
    void SignReqCert::setVersion(int version) {
        int success = X509_set_version(_crt.get(), 0);

        if (success != 1) {
            LOG_ERROR("SignReqCert::SignReqCert", "Failed to set version client _crt");
        }
    }
    
    void SignReqCert::setSerialNumber(int serial_number) {
        ASN1_INTEGER *serial = ASN1_INTEGER_new();
        ASN1_INTEGER_set(serial, serial_number);    
        X509_set_serialNumber(_crt.get(), serial);
        ASN1_INTEGER_free(serial);
    }
    
    void SignReqCert::makeSign(EVP_PKEY *pkey, X509 *ca_cert, X509_REQ *client_req,
            const EVP_MD *method, long elapse_time_before, long elapse_time_after) {
        
        X509_set_issuer_name(_crt.get(), X509_get_subject_name(ca_cert));

        X509_gmtime_adj(X509_get_notBefore(_crt.get()), elapse_time_before);
        X509_gmtime_adj(X509_get_notAfter(_crt.get()), elapse_time_after /*2*365*3600*/);

        X509_set_subject_name(_crt.get(), X509_REQ_get_subject_name(client_req));

        EVP_PKEY* req_pubkey = X509_REQ_get_pubkey(client_req);
        X509_set_pubkey(_crt.get(), req_pubkey);
        EVP_PKEY_free(req_pubkey);

        int success = X509_sign(_crt.get(), pkey, method /* EVP_sha256() */);
        
        if (success == 0) {
            LOG_ERROR("SignReqCert::makeSign", "Failed to sign client _crt");
        }
    }
   
    void SignReqCert::save(const char *file_name) {
        BIO* bio = BIO_new_file(file_name, "wb");
        int success = PEM_write_bio_X509(bio, _crt.get());
        
        if (success != 1) {
            LOG_ERROR("SignReqCert::save", "Failed to save request crt in file");
        }

        BIO_free(bio);
    }

    X509* SignReqCert::getCertificate() const {
        return _crt.get();
    }

}