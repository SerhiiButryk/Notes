#include "register_handler.h"

#include "storage/system_storage.h"
#include "storage/file_system.h"
#include "storage/cashe_manager.h"
#include "app_common/env_constants.h"
#include "crypto/hash.h"
#include "app/logic/action_dispatcher.h"
#include "utils/log.h"
#include "app/core/utils/auth_utils.h"
#include "app/logic/base/system_constants.h"

using namespace MYLIB;

const static std::string TAG = "RegistrationHandler";

namespace APP
{

    RegisterHandler::RegisterHandler() : EventHandlerBase(SYSTEM_EVENT::REGISTER_ACCOUNT)
    {

    }

    void RegisterHandler::handleEvent(const Event<SYSTEM_EVENT>& event)
    {
        Log::Info(TAG, "handleEvent() : got event");

        std::string username = event.getData(USERNAME_KEY);
        std::string password = event.getData(PASSWORD_KEY);
        std::string confirmPassword = event.getData(CONFIRM_PASSWORD_KEY);

        auto success = AuthUtils::checkRules(password, confirmPassword, username, true);

        if (success == ACTION_TYPE ::NO_ERRORS)
        {
            std::map<std::string, std::string> data;
            data.insert(std::make_pair(username, makeHashMD5(password)));

            SystemStorage ss;

            // Save user credentials
            if (FileSystem::getInstance()->isExists(kFileSystemData))
            {
                if (AuthUtils::isUserAccountExists(username))
                {
                    sendSystemAction(ACTION_TYPE::USER_NAME_EXISTS);

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

            sendSystemAction(ACTION_TYPE::REGISTRATION_DONE);

            return;
        }

        sendSystemAction(success);
    }

}