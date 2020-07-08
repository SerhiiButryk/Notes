#include "auth_utils.h"

#include <map>

#include "app_common/env_constants.h"
#include "storage/system_storage.h"
#include "utils/algorithms.h"

using MYLIB::Algorithms;
using MYLIB::SystemStorage;

bool APP::AuthUtils::isUserAccountExists(const std::string& user_name)
{
    SystemStorage ss;

    std::map<std::string, std::string> data = ss.getValues(kFileSystemData);

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

    std::map<std::string, std::string> data = ss.getValues(kFileSystemData);

    for (auto& p : data)
    {
        if (p.first == user_name && p.second == user_password) {
            return true;
        }

    }

    return false;
}

APP::EVENT_RESULT APP::AuthUtils::checkRules(const std::string &password, const std::string &confirm_password,
                                             const std::string &email, bool check_confirm)
{
    if (password.empty() || email.empty())
    {
        return EVENT_RESULT::EMPTY_FIELD;
    }

    if (check_confirm)
    {

        if (confirm_password.empty())
        {
            return EVENT_RESULT::EMPTY_FIELD;
        }

        if (password != confirm_password)
        {
            return EVENT_RESULT::PASSWORD_DIFFERS;
        }

        if (Algorithms::containSpace(password)
            || Algorithms::containSpace(confirm_password)
            || Algorithms::containSpace(email))
        {
            return EVENT_RESULT::SPACE_CONTAIN;
        }

        return EVENT_RESULT::NO_ERRORS;
    }

    return EVENT_RESULT::NO_ERRORS;
}

void APP::AuthUtils::setLoginLimit()
{
    SystemStorage ss;

    if (ss.doesKeyExist(kFileSystemData, kUserLoginAttempts)) {
        return;
    }

    std::map<std::string, std::string> value;
    value.insert(std::make_pair(kUserLoginAttempts, std::to_string(USER_LOGIN_ATTEMPTS_DEFAULT)));
    value.insert(std::make_pair(kUserLoginAttemptsLeft, std::to_string(USER_LOGIN_ATTEMPTS_DEFAULT)));

    ss.addValues(kFileSystemData, value);
}

void APP::AuthUtils::setIdleLockTimeOut()
{
    SystemStorage ss;

    if (ss.doesKeyExist(kFileSystemData, kIdleLockTimeOut)) {
        return;
    }

    std::map<std::string, std::string> value;
    value.insert(std::pair(kIdleLockTimeOut, std::to_string(IDLE_LOCK_TIMEOUT_DEFAULT)));

    ss.addValues(kFileSystemData, value);
}
