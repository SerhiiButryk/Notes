#pragma once

namespace APP
{

    // TODO: maybe create class instead ?
    enum class SYSTEM_EVENT
    {
        AUTHORIZE = 101,
        REGISTER_ACCOUNT,
        UNLOCK
    };

    // TODO: maybe create class instead ?
    enum class EVENT_RESULT
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

        NO_ERRORS = 1
    };
}
