#pragma once

#include <string>
#include <vector>
#include <memory>

#include "app_common/types.h"
#include "app/logic/base/event.h"
#include "app/logic/base/event_handler_base.h"

namespace APP
{
    /**
     *  This class knows about all event handlers.
     *
     *  Use it to deliver an event to corresponding event handler (receiver)
     */
    class SystemEventReceiver
    {
        private:
            std::vector<std::unique_ptr<EventHandlerBase<SYSTEM_EVENT>>> receivers;

        public:
            static SystemEventReceiver* getInstance();

            void forward(const Event<SYSTEM_EVENT>& event);

            void addReceiver(EventHandlerBase<SYSTEM_EVENT>* receiver);

        private:
            SystemEventReceiver();

    };

}
