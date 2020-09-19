#pragma once

#include "app/logic/base/event_handler_base.h"

namespace APP
{
    /**
     *  Class which handles SYSTEM_EVENT::REGISTER_ACCOUNT
     */

    class RegisterHandler : public EventHandlerBase<SYSTEM_EVENT>
    {
        public:
            RegisterHandler();

            void handleEvent(const Event<SYSTEM_EVENT>& event) override;

    };
}


