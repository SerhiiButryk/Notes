#include "register_handler.h"

#include "storage/system_storage.h"
#include "storage/file_system.h"
#include "storage/cashe_manager.h"
#include "app/logic/base/env_constants.h"
#include "app/logic/action_dispatcher.h"
#include "utils/log.h"
#include "app/logic/utils/auth_utils.h"
#include "app/logic/base/system_constants.h"
#include "crypto/hash.h"

using namespace MYLIB;

const static std::string TAG = "RegistrationHandler";

namespace APP
{

    RegisterHandler::RegisterHandler() : EventHandlerBase(SYSTEM_EVENT::REGISTER_ACCOUNT)
    {

    }

    bool RegisterHandler::handleEvent(const Event<SYSTEM_EVENT>& event)
    {
        Log::Info(TAG, "handleEvent() : got event");

        std::string username = event.getData(USERNAME_KEY);
        std::string password = event.getData(PASSWORD_KEY);

        std::map<std::string, std::string> data;
        data.insert(std::make_pair(username,password));

        SystemStorage ss;

        // Save user credentials
        if (FileSystem::getInstance()->isExists(kFileSystemData))
        {
            if (AuthUtils::isUserAccountExists(username))
            {
                Log::Info(TAG, "handleEvent() : user is already present");
            }

            ss.clearData(kFileSystemData);
            ss.addData(kFileSystemData, data);

        } else {

            if (ss.createFile(kFileSystemData))
            {
                ss.addData(kFileSystemData, data);
            }

        }

        // User is registered, cache user name
        CacheManager cacheManager;
        cacheManager.cache(kFileCashSystemData, kUserName, username);

        ActionDispatcher::getInstance()->sendMessage(SYSTEM_MESSAGE::REGISTRATION_DONE);

        return true;
    }

}