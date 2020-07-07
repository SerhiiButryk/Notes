#include "unlock_handler.h"

#include "app_common/env_constants.h"
#include "app/logic/event_dispatcher.h"
#include "storage/system_storage.h"

using MYLIB::SystemStorage;

namespace APP
{

    UnlockHandler::UnlockHandler() : EventHandlerBase(SYSTEM_EVENT::UNLOCK)
    {

    }

    void UnlockHandler::handleEvent(const Event<SYSTEM_EVENT>& event)
    {
        std::string unlockKey = event.getData(UNLOCK_KEY);

        if (unlockKey != UNLOCK_ACCESSKEY_DEFAULT)
        {
            sendSystemEvent(EVENT_RESULT::UNLOCK_KEY_INVALID);

        } else {

            SystemStorage ss;
            ss.updateValue(kFileSystemData, kIsUserBlocked, FALSE);

            sendSystemEvent(EVENT_RESULT::UNLOCK_DONE);
        }

    }

    const std::string UnlockHandler::UNLOCK_KEY = "unlock_key";

}
