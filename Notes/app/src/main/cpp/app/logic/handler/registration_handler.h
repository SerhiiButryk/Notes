#pragma once

#include "app/logic/system/event_handler_base.h"

namespace APP
{
    /**
     *  Class which handles SYSTEM_EVENT::REGISTER_ACCOUNT
     */

    class RegistrationHandler : public EventHandlerBase<SYSTEM_EVENT>
    {
        public:
            RegistrationHandler();

            void handleEvent(const Event<SYSTEM_EVENT>& event) override;

    };
}


