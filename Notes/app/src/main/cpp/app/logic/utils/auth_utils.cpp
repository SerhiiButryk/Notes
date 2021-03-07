#include "auth_utils.h"

#include <map>

#include "app/logic/base/env_constants.h"
#include "storage/system_storage.h"
#include "utils/algorithms.h"

using namespace MYLIB;

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

APP::SYSTEM_MESSAGE APP::AuthUtils::checkRules(const std::string &password, const std::string &confirm_password,
                                               const std::string &email, bool check_confirm)
{
    if (password.empty() || email.empty())
    {
        return SYSTEM_MESSAGE::EMPTY_FIELD;
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
