#pragma once

#include "app/logic/system/event_handler_base.h"

namespace APP
{
    /**
     *  Class which handles SYSTEM_EVENT::AUTHORIZE
     */

    class AuthorizeHandler : public EventHandlerBase<SYSTEM_EVENT>
    {
        public:
            AuthorizeHandler();

            void handleEvent(const Event<SYSTEM_EVENT>& event) override;

            static const std::string USERNAME_KEY;
            static const std::string PASSWORD_KEY;
    };

}

