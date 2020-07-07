#include "system_event_receiver.h"

#include "app/logic/handler/authorize_handler.h"
#include "app/logic/handler/registration_handler.h"
#include "app/logic/handler/unlock_handler.h"

namespace APP
{
    SystemEventReceiver* SystemEventReceiver::getInstance()
    {
        static SystemEventReceiver requestDispatcher;
        return &requestDispatcher;
    }

    void SystemEventReceiver::forward(const Event<SYSTEM_EVENT>& event)
    {
        for (auto& r : receivers)
        {
            if (r->getEventType() == event.getEventType())
            {
                r->handleEvent(event);
            }
        }
    }

    void SystemEventReceiver::addReceiver(EventHandlerBase<SYSTEM_EVENT>* receiver)
    {
        std::unique_ptr<EventHandlerBase<SYSTEM_EVENT>> ptr(receiver);

        receivers.push_back(std::move(ptr));
    }

    SystemEventReceiver::SystemEventReceiver()
    {
        AuthorizeHandler* authorizeHandler = new AuthorizeHandler();
        RegistrationHandler* registrationHandler = new RegistrationHandler();
        UnlockHandler* unlockHandler = new UnlockHandler();

        addReceiver(authorizeHandler);
        addReceiver(registrationHandler);
        addReceiver(unlockHandler);
    }

}
