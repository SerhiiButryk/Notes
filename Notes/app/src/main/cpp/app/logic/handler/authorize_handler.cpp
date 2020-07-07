#include "authorize_handler.h"

#include "app_common/types.h"
#include "app/logic/event_dispatcher.h"
#include "storage/system_storage.h"
#include "app_common/env_constants.h"
#include "app/core/utils/auth_utils.h"

using MYLIB::SystemStorage;

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
                AuthUtils::setUserAuthorized(true);

                sendSystemEvent(EVENT_RESULT::AUTHORIZATION_DONE);

                return;

            } else {

                sendSystemEvent(EVENT_RESULT::WRONG_PASSWORD);

                return;
            }

        }
    }

    sendSystemEvent(EVENT_RESULT::ACCOUNT_INVALID);
}

const std::string APP::AuthorizeHandler::USERNAME_KEY = "username key";

const std::string APP::AuthorizeHandler::PASSWORD_KEY = "password key";