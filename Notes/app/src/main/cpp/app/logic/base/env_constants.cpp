#include "env_constants.h"

namespace APP
{
    const std::string kFileSystemData = "startup_data_file.txt";

    const std::string kFileCashSystemData = "cache_data.txt";

    const std::string kUserName = "k_cached_user";

    const std::string kUserLoginAttempts = "k_login_attempts";

    const std::string kUserLoginAttemptsLeft = "k_login_attempts_left";

    const std::string kIsUserBlocked = "k_is_user_blocked";

    const std::string kIdleLockTimeOut = "k_idle_lock_time_out";

    const int USER_LOGIN_ATTEMPTS_DEFAULT = 5;

    const int IDLE_LOCK_TIMEOUT_DEFAULT = 300000; // 5 minutes

    extern const std::string UNLOCK_ACCESSKEY_DEFAULT = "1a3f732ee27ee78c8aaa0c8b73933a4"; // Hash

    const std::string TRUE = "1";
    const std::string FALSE = "0";
}