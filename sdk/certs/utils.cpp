#include "utils.h"

namespace MYLIB 
{

    PKCS12* Utils::convertToP12(RSA *client_rsa_key, const char *password, const char *name,
        X509 *crt, STACK_OF(X509) *cacert_stack = 0) {
        
        PKCS12* pk12 = 0;
        char* pass = const_cast<char*>(password);
        char* n = const_cast<char*>(name);
        EVP_PKEY* client_pkey = EVP_PKEY_new();
        EVP_PKEY_assign_RSA(client_pkey, client_rsa_key);
        pk12 = PKCS12_create(
                pass,                     // certbundle access password
                n,                        // friendly certname
                client_pkey,              // the certificate private key
                crt,                      // the main certificate
                cacert_stack,             // stack of CA cert chain
                0,                        // int nid_key (default 3DES)
                0,                        // int nid_cert (40bitRC2)
                0,                        // int iter (default 2048)
                0,                        // int mac_iter (default 1)
                0                         // int keytype (default no flag)
        );
        if (!pk12) {
            LOG_ERROR("Helpers::convertToP12", "Failed to convert");
        } 
        return pk12;
    }
    
    void Utils::saveFileP12(const char *file_name, const PKCS12* pk12) {
        FILE* f = fopen(file_name, "w");
        if (!f) {
            LOG_ERROR("Helpers::saveFileP12", "Failed to open file");
            fclose(f);
            return;
        }
        int success = i2d_PKCS12_fp(f, const_cast<PKCS12*>(pk12));
        if (success <= 0) {
            LOG_ERROR("Helpers::saveFileP12", "Failed to save file");
            fclose(f);
            return;
        }
        fclose(f);
    }
    
    X509* Utils::readX509(const char* file_name) {
        
        FILE* f = fopen(file_name, "r");
        
        if (!f) {
            LOG_ERROR("Helpers::readX509", "Failed to open file");
            fclose(f);
            return nullptr;
        }
        X509 *cert = PEM_read_X509(f, NULL, NULL, NULL);
        if (!cert) {
            LOG_ERROR("Helpers::readX509", "Failed to read file");
            fclose(f);
            return nullptr;
        }
        fclose(f);
        return cert;
    }
    
    /** Read from memory buffer */
    X509* Utils::readRawX509(const unsigned char* data, long length) {
        X509 *cert = d2i_X509(NULL, &data, length);
       
        if (!cert) {
            LOG_ERROR("Helpers::readRawX509", "Failed to read raw data");
            return nullptr;
        }
       
        return cert;
    }

    // Cert managment
    std::string Utils::getCertificateIssuer(const X509* crt) {
        char *issuer = X509_NAME_oneline(X509_get_issuer_name(crt), NULL, 0);
        return std::string(issuer);
    }
    
    std::string Utils::getCertificateSubject(const X509* crt) {
        char *subj = X509_NAME_oneline(X509_get_subject_name(crt), NULL, 0);
        return std::string(subj);
    }
    
    int Utils::getVersion(const X509* crt) {
        return X509_get_version(crt) + 1;
    }
}