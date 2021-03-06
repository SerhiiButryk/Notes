#pragma once

#include "app/logic/base/event_handler_base.h"

namespace APP
{
    class UnlockHandler : public EventHandlerBase<SYSTEM_EVENT>
    {
        public:
            UnlockHandler();

            void handleEvent(const Event<SYSTEM_EVENT>& event) override;

    };
}
