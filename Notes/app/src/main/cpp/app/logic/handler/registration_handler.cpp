#include "registration_handler.h"

#include "storage/system_storage.h"
#include "storage/file_system.h"
#include "storage/cashe_manager.h"
#include "app_common/env_constants.h"
#include "crypto/hash.h"
#include "app/logic/event_dispatcher.h"
#include "utils/log.h"
#include "app/core/utils/auth_utils.h"
#include "app/logic/system/common_constants.h"

using MYLIB::makeHashMD5;
using MYLIB::CacheManager;
using MYLIB::SystemStorage;
using MYLIB::FileSystem;
using MYLIB::Log;

const static std::string TAG = "RegistrationHandler";

namespace APP
{

    RegistrationHandler::RegistrationHandler() : EventHandlerBase(SYSTEM_EVENT::REGISTER_ACCOUNT)
    {

    }

    void RegistrationHandler::handleEvent(const Event<SYSTEM_EVENT>& event)
    {
        std::string username = event.getData(USERNAME_KEY);
        std::string password = event.getData(PASSWORD_KEY);
        std::string confirmPassword = event.getData(CONFIRM_PASSWORD_KEY);

        auto success = AuthUtils::checkRules(password, confirmPassword, username, true);

        if (success == EVENT_RESULT ::NO_ERRORS)
        {
            std::map<std::string, std::string> data;
            data.insert(std::make_pair(username, makeHashMD5(password)));

            SystemStorage ss;

            // Save user credentials
            if (FileSystem::getInstance()->isExists(kFileSystemData))
            {
                if (AuthUtils::isUserAccountExists(username))
                {
                    sendSystemEvent(EVENT_RESULT ::USER_NAME_EXISTS);

                    return;
                }

                ss.addValues(kFileSystemData, data);

            } else {

                if (ss.createFile(kFileSystemData))
                {
                    ss.addValues(kFileSystemData, data);
                }

            }

            // User is registered, cash user name
            CacheManager cacheManager;
            cacheManager.cash(kFileCashSystemData, kUserName, username);

            AuthUtils::setLoginLimit();

            Log::Info(TAG, "handleEvent() - REGISTRATION_DONE");

            sendSystemEvent(EVENT_RESULT::REGISTRATION_DONE);

            return;
        }

        sendSystemEvent(success);
    }

}