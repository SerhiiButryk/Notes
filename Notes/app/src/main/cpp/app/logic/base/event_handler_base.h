#pragma once

#include "event.h"
#include "types.h"

namespace APP
{

    template <class T>
    class EventHandlerBase
    {
        public:
            explicit EventHandlerBase(T eventType);
            virtual ~EventHandlerBase() = default;

            T getEventType() const;

            virtual void handleEvent(const Event<T>& event) = 0;

        private:
            T event;
    };

    template <class T>
    EventHandlerBase<T>::EventHandlerBase(T eventType) : event(eventType)
    {

    }

    template <class T>
    T EventHandlerBase<T>::getEventType() const
    {
        return event;
    }

}

