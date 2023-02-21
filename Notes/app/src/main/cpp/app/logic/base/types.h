#pragma once

namespace APP
{
    enum class SYSTEM_EVENT
    {
        AUTHORIZE = 101,
        REGISTER_ACCOUNT,
        UNLOCK
    };

    enum class SYSTEM_MESSAGE
    {
        EMPTY_FIELD = -1,
        WRONG_PASSWORD = -2,
        /**
         * User account doesn't exist
         */
        ACCOUNT_INVALID = -3,
        /**
         * Can't register a new user account
         */
        USER_NAME_EXISTS = -4,
        PASSWORD_DIFFERS = -5,
        SPACE_CONTAIN = -6,

        AUTHORIZATION_DONE = -7,
        REGISTRATION_DONE = -8,
        UNLOCK_DONE = -9,

        UNLOCK_KEY_INVALID = -10,

        UNLOCK_KEYSTORE = -11,

        PASSWORD_IS_WEAK = -12,

        NO_ERRORS = 1
    };
}
