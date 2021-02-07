#pragma once

#include <string>

#include "app/logic/base/types.h"

namespace APP
{
    class AuthUtils
    {
        public:
            static bool isUserAccountExists(const std::string& user_name);

            static bool verifyUserPassword(const std::string& user_name, const std::string& user_password);

            static SYSTEM_MESSAGE checkRules(const std::string& password, const std::string& confirm_password,
                                             const std::string& email, bool check_confirm = false);

            static void setLoginLimit();
    };
}
