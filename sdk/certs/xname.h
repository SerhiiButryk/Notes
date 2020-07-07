#ifndef _CRYPTO_XNAME_H_
#define _CRYPTO_XNAME_H_

#include <openssl/x509.h>
#include <memory>

namespace MYLIB 
{
    class XName 
    {
        public:

            //  C = country
            //  ST = state
            //  L = locality
            //  O = organisation
            //  OU = organisational unit
            //  CN = common name
            //  GN = given name
            //  SN = surname
            //  emailAddress = bob@example.com

            XName();
            ~XName() = default;

            XName& setCountry(const std::string& country);
            XName& setState(const std::string& state);
            XName& setLocality(const std::string& locality);
            XName& setOrganization(const std::string& organization);
            XName& setOrganizationUnit(const std::string& organization_unit);
            XName& setCommonName(const std::string& common_name);
            XName& setGivenName(const std::string& given_name);
            XName& setGivenSurname(const std::string& given_surname);
            XName& setEmail(const std::string& email);
            XName& setSubjectName(const X509* crt);
            XName& setSubjectNameRequest(const X509_REQ* req);
            XName& setIssuerName(X509* crt);

            X509_NAME* build();

        private:

            std::unique_ptr<X509_NAME, decltype(&X509_NAME_free)> _name;
    };

};

#endif //CRYPTO_XNAME_H
