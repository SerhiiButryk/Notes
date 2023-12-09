#include "auth_utils.h"

#include <map>
#include <regex>

#include "app/logic/base/env_constants.h"
#include "storage/system_storage.h"
#include "utils/algorithms.h"
#include "utils/log.h"

using namespace MYLIB;

const int MIN_PASSWORD_LENGTH = 6; // Min password length
const std::string TAG = "AuthUtils";

bool APP::AuthUtils::isUserAccountExists(const std::string& user_name)
{
    SystemStorage ss;

    std::map<std::string, std::string> data = ss.readData(kFileSystemData);

    for (const auto& p : data)
    {
        if (p.first == user_name) {
            return true;
        }
    }

    return false;
}

bool APP::AuthUtils::verifyUserPassword(const std::string& user_name, const std::string& user_password)
{
    SystemStorage ss;

    std::map<std::string, std::string> data = ss.readData(kFileSystemData);

    for (auto& p : data)
    {
        if (p.first == user_name && p.second == user_password) {
            return true;
        }

    }

    return false;
}

APP::SYSTEM_MESSAGE APP::AuthUtils::checkRules(const std::string &password, const std::string &confirm_password,
                                               const std::string &email, bool check_confirm)
{

    if (password.size() < MIN_PASSWORD_LENGTH)
    {
        return SYSTEM_MESSAGE::PASSWORD_IS_WEAK;
    }

    if (password.empty() || email.empty())
    {
        return SYSTEM_MESSAGE::EMPTY_FIELD;
    }

    if (!isEmailValid(email))
    {
        return SYSTEM_MESSAGE::EMAIL_INVALID;
    }

    if (check_confirm)
    {

        if (confirm_password.empty())
        {
            return SYSTEM_MESSAGE::EMPTY_FIELD;
        }

        if (password != confirm_password)
        {
            return SYSTEM_MESSAGE::PASSWORD_DIFFERS;
        }

        if (Algorithms::containSpace(password)
            || Algorithms::containSpace(confirm_password)
            || Algorithms::containSpace(email))
        {
            return SYSTEM_MESSAGE::SPACE_CONTAIN;
        }

        return SYSTEM_MESSAGE::NO_ERRORS;
    }

    return SYSTEM_MESSAGE::NO_ERRORS;
}

bool APP::AuthUtils::isEmailValid(const std::string& email)
{
    // Check if the entered text has correct email pattern
    const std::regex email_regex(R"(^[\w\.-]+@[\w-]+\.[a-zA-Z]{2,4}$)");
    std::smatch base_match;

    if (std::regex_match(email, base_match, email_regex))
    {
        Log::Info(TAG, "AuthUtils::isEmailValid() passed");
        return true;
    }

    Log::Info(TAG, "AuthUtils::isEmailValid() not passed");
    return false;
}