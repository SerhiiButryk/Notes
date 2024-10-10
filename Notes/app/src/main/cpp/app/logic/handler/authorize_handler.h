#pragma once

#include "app/logic/base/event_handler_base.h"

namespace APP
{
    /**
     *  Class which handles SYSTEM_EVENT::AUTHORIZE
     */

    class AuthorizeHandler : public EventHandlerBase<SYSTEM_EVENT>
    {
        public:
            AuthorizeHandler();

            bool handleEvent(const Event<SYSTEM_EVENT>& event) override;
    };

}

