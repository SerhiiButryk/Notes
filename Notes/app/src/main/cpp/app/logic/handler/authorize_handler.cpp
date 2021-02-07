#include "authorize_handler.h"

#include "app/logic/base/types.h"
#include "app/logic/action_dispatcher.h"
#include "storage/system_storage.h"
#include "app/logic/base/env_constants.h"
#include "app/logic/utils/auth_utils.h"
#include "app/logic/base/system_constants.h"

using namespace MYLIB;

APP::AuthorizeHandler::AuthorizeHandler() : EventHandlerBase(SYSTEM_EVENT::AUTHORIZE)
{

}

void APP::AuthorizeHandler::handleEvent(const Event<SYSTEM_EVENT>& event)
{
    SystemStorage ss;

    std::map<std::string, std::string> data = ss.getValues(kFileSystemData);

    for (const auto& p : data)
    {
        if (p.first == event.getData(USERNAME_KEY)) {

            if (p.second == event.getData(PASSWORD_KEY))
            {
                ActionDispatcher::getInstance()->sendMessage(SYSTEM_MESSAGE::AUTHORIZATION_DONE);

                return;

            } else {

                ActionDispatcher::getInstance()->sendMessage(SYSTEM_MESSAGE::WRONG_PASSWORD);

                return;
            }

        }
    }

    ActionDispatcher::getInstance()->sendMessage(SYSTEM_MESSAGE::ACCOUNT_INVALID);
}
