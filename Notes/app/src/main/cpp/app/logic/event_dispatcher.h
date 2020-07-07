#pragma once

#include <vector>

#include "utils/jni_wrpper.h"
#include "app/logic/receiver/system_event_receiver.h"
#include "app/logic/system/action_interface.h"

namespace APP
{

    /**
     *  This class received events from Native side and notify about it another interested parties.
     *
     *  All result of any requests or actions should go through this class.
     */

    class EventDispatcher : private IAuthorization, private IRegistration, private IShowDialog
    {
        private:
            std::vector<IAuthorization*> authorization_observers;
            std::vector<IRegistration*> registration_observers;
            std::vector<IShowDialog*> showdialog_observers;

        public:
            static EventDispatcher* getInstance();

            void sendEvent(EVENT_RESULT event);

            void addAuthorizeEventObserver(IAuthorization* observer);
            void removeAuthorizeObserver(IAuthorization* observer);

            void addRegistrationEventObserver(IRegistration* observer);
            void removeRegistrationObserver(IRegistration* observer);

            void addShowDialogEventObserver(IShowDialog* observer);
            void removeShowDialogObserver(IShowDialog* observer);

        private:
            /**
             * Native callbacks
             */
            void onAuthorized() override;
            void onRegistered() override;
            void onShowDialog(int type) override;
    };

    /**
     * Helper functions
     */
    void sendSystemEvent(EVENT_RESULT event);

}
