#include "ca_cert.h"

#include <openssl/pem.h>
#include <string>

#include "../common/log.h"

namespace MYLIB 
{

    CACert::CACert() : _name(), _crt(X509_new(), X509_free) { }

    void CACert::setSerialNumber(unsigned serial_number) {
        ASN1_INTEGER_set(X509_get_serialNumber(_crt.get()), serial_number);
    }
    
    void CACert::setElapseTime(long elapse_time_before, long elapse_time_after) {
        X509_gmtime_adj(X509_get_notBefore(_crt.get()), elapse_time_before);
        X509_gmtime_adj(X509_get_notAfter(_crt.get()), elapse_time_after);
    }
    
    void CACert::makeSelfSign(const EVP_MD *method, EVP_PKEY* pkey) {

        int success = X509_set_pubkey(_crt.get(), pkey);

        if (success != 1) {
            LOG_ERROR("CACert::makeSelfSign", "Failed to set public key");
        }

        success = X509_sign(_crt.get(), pkey, method);

        if (success <= 0) {
            LOG_ERROR("CACert::makeSelfSign", "Failed to sign");
        }
    }

    void CACert::save(const char *file_name) {
        FILE* f = fopen(file_name, "wb");

        if (!f) {
            LOG_ERROR("CACert::save", "Failed to open file");
            fclose(f);
            return;
        }

        int success = PEM_write_X509(f, _crt.get() );
        
        if (success != 1) {
            LOG_ERROR("CACert::save", "Failed to save X509 file");
        }

        fclose(f);
    }  

    X509* CACert::getCertificate() const {
        return _crt.get();
    }

    void CACert::setNameAttributes(const char *country_code, const char *company, const char *host) {
        _name.setSubjectName(_crt.get())
                .setCountry(country_code)
                .setOrganization(company)
                .setCommonName(host)
                .setIssuerName(_crt.get());
    }

}