#include "unlock_handler.h"

#include "app/logic/base/env_constants.h"
#include "app/logic/action_dispatcher.h"
#include "storage/system_storage.h"
#include "app/logic/base/system_constants.h"

using namespace MYLIB;

namespace APP
{

    UnlockHandler::UnlockHandler() : EventHandlerBase(SYSTEM_EVENT::UNLOCK)
    {

    }

    bool UnlockHandler::handleEvent(const Event<SYSTEM_EVENT>& event)
    {
        std::string unlockKey = event.getData(UNLOCK_KEY);
        std::string currentUnlockKey = event.getData(CURRENT_UNLOCK_KEY);

        if (unlockKey != currentUnlockKey)
        {
            ActionDispatcher::getInstance()->sendMessage(SYSTEM_MESSAGE::UNLOCK_KEY_INVALID);

        } else {

            SystemStorage ss;
            ss.updateData(kFileSystemData, kIsUserBlocked, FALSE);

            ActionDispatcher::getInstance()->sendMessage(SYSTEM_MESSAGE::UNLOCK_DONE);

            return true;
        }

        return false;
    }

}
