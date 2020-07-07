#pragma once

#include <string>

#include "app_common/types.h"

namespace APP
{
    class AuthUtils
    {
        private:
            static bool is_user_authorized;

        public:
            static bool isUserAccountExists(const std::string& user_name);

            static bool verifyUserPassword(const std::string& user_name, const std::string& user_password);

            static bool isUserAuthorized();
            static void setUserAuthorized(bool isUserAuthorized);

            static EVENT_RESULT checkRules(const std::string& password, const std::string& confirm_password,
                                           const std::string& email, bool check_confirm = false);

            static void setLoginLimit();
            static void setIdleLockTimeOut();
    };
}
