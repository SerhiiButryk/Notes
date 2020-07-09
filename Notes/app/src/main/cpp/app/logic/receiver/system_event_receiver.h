#pragma once

#include <string>
#include <vector>
#include <memory>

#include "app_common/types.h"
#include "app/logic/system/event.h"
#include "app/logic/system/event_handler_base.h"

namespace APP
{
    /**
     *  Class processes 'SYSTEM_EVENT' event types and forward them to
     *
     *  corresponding event handler (receiver).
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
