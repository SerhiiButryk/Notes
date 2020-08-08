#include "unlock_handler.h"

#include "app_common/env_constants.h"
#include "app/logic/action_dispatcher.h"
#include "storage/system_storage.h"
#include "app/logic/base/system_constants.h"

using namespace MYLIB;

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
            sendSystemAction(ACTION_TYPE::UNLOCK_KEY_INVALID);

        } else {

            SystemStorage ss;
            ss.updateValue(kFileSystemData, kIsUserBlocked, FALSE);

            sendSystemAction(ACTION_TYPE::UNLOCK_DONE);
        }

    }

}
