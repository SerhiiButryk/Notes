#pragma once

#include <string>

namespace APP
{
    // System file names
    extern const std::string kFileSystemData;
    extern const std::string kFileCashSystemData;

    // Key value names
    extern const std::string kUserName;
    extern const std::string kUserLoginAttempts;
    extern const std::string kUserLoginAttemptsLeft;
    extern const std::string kIsUserBlocked;

    // Defaults constants
    extern const int USER_LOGIN_ATTEMPTS_DEFAULT;
    extern const int IDLE_LOCK_TIMEOUT_DEFAULT;
    extern const std::string UNLOCK_ACCESSKEY_DEFAULT; // TODO: Temporary, replace this hard-codded value

    extern const std::string TRUE;
    extern const std::string FALSE;
}
