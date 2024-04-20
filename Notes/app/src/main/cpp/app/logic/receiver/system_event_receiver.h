#pragma once

#include <string>
#include <vector>
#include <memory>

#include "app/logic/base/types.h"
#include "app/logic/base/event.h"
#include "app/logic/base/event_handler_base.h"

namespace APP
{
    /**
     *  Class to manage event processing in the cpp code.
     *
     *  This class sends events to corresponding event handler.
     */
    class SystemEventReceiver
    {
        private:
            std::vector<std::unique_ptr<EventHandlerBase<SYSTEM_EVENT>>> receivers;

        public:
            static SystemEventReceiver* getInstance();

            bool forward(const Event<SYSTEM_EVENT>& event);

            void addReceiver(EventHandlerBase<SYSTEM_EVENT>* receiver);

        private:
            SystemEventReceiver();

    };

}
