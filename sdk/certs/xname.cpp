#include "xname.h"
#include "../common/log.h"

namespace MYLIB 
{

    XName::XName() : _name(X509_NAME_new(), X509_NAME_free) {}

    XName &XName::setCountry(const std::string &country) {

        int success = X509_NAME_add_entry_by_txt(_name.get(), "C",  MBSTRING_ASC /* type of byte fields */,
            (unsigned char*)country.c_str(), -1 /* length calculated internally */, 
            -1 /* somehow we can specify specific position */, 0);

        if (success != 1) {
            LOG_ERROR("setCountry", "Failed to set country");
        }

        return *this;
    }

    XName &XName::setState(const std::string &state) {

        int success = X509_NAME_add_entry_by_txt(_name.get(), "ST",  MBSTRING_ASC,
                                   (unsigned char*)state.c_str(), -1, -1, 0);

        if (success != 1) {
            LOG_ERROR("XName::setState", "Failed to set state");
        }

        return *this;
    }

    XName &XName::setLocality(const std::string &locality) {

        int success = X509_NAME_add_entry_by_txt(_name.get(), "L",  MBSTRING_ASC,
            (unsigned char*) locality.c_str(), -1, -1, 0);

        if (success != 1) {
            LOG_ERROR("XName::setLocality", "Failed to set locality");
        }

        return *this;
    }

    XName &XName::setOrganization(const std::string &organization) {

        int success = X509_NAME_add_entry_by_txt(_name.get(), "O",  MBSTRING_ASC,
            (unsigned char*)organization.c_str(), -1, -1, 0);

        if (success != 1) {
            LOG_ERROR("XName::setOrganization", "Failed to set organization");
        }

        return *this;
    }

    XName &XName::setOrganizationUnit(const std::string &organization_unit) {

        int success = X509_NAME_add_entry_by_txt(_name.get(), "O",  MBSTRING_ASC,
                                                 (unsigned char*)organization_unit.c_str(), -1, -1, 0);
        if (success != 1) {
            LOG_ERROR("XName::setOrganizationUnit", "Failed to set organization unit");
        }

        return *this;
    }

    XName &XName::setCommonName(const std::string &common_name) {

        int success = X509_NAME_add_entry_by_txt(_name.get(), "CN",  MBSTRING_ASC,
            (unsigned char*) common_name.c_str(), -1, -1, 0);
        
        if (success != 1) {
            LOG_ERROR("XName::setCommonName", "Failed to set common name");
        }

        return *this;
    }

    XName &XName::setGivenName(const std::string &given_name) {

        int success = X509_NAME_add_entry_by_txt(_name.get(), "GN",  MBSTRING_ASC,
            (unsigned char*) given_name.c_str(), -1, -1, 0);

        if (success != 1) {
            LOG_ERROR("XName::setGivenName", "Failed to set given name");
        }

        return *this;
    }

    XName &XName::setGivenSurname(const std::string &given_surname) {

        int success = X509_NAME_add_entry_by_txt(_name.get(), "SN",  MBSTRING_ASC,
            (unsigned char*)given_surname.c_str(), -1, -1, 0);

        if (success != 1) {
            LOG_ERROR("XName::setGivenSurname", "Failed to set given surname");
        }

        return *this;
    }

    XName &XName::setEmail(const std::string &email) {

        int success = X509_NAME_add_entry_by_txt(_name.get(), "emailAddress",  MBSTRING_ASC,
            (unsigned char*)email.c_str(), -1, -1, 0);

        if (success != 1) {
            LOG_ERROR("XName::setEmail", "Failed to set email");
        }

        return *this;
    }

    X509_NAME *XName::build() {
        return _name.get();
    }

    XName &XName::setSubjectName(const X509 *crt) {
        _name.reset(X509_get_subject_name(crt));
        return *this;
    }

    XName &XName::setIssuerName(X509 *crt) {
        X509_set_issuer_name(crt, _name.get());
        return *this;
    }

    XName &XName::setSubjectNameRequest(const X509_REQ *req) {
        _name.reset(X509_REQ_get_subject_name(req));
        return *this;
    }

}