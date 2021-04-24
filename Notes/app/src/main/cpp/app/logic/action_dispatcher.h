#pragma once

#include <vector>

#include "utils/jni_wrpper.h"
#include "app/logic/receiver/system_event_receiver.h"
#include "app/logic/base/system_action.h"

namespace APP
{

    /**
     *  This class controls app actions corresponding to system events.
     *
     *  It processes the result of event handling and dispatches an action if it's needed.
     *
     *  All system messages should go through this class.
     */

    class ActionDispatcher
    {
        private:
            std::vector<SystemActions*> authorization_observers;
            std::vector<SystemActions*> registration_observers;
            std::vector<SystemActions*> showdialog_observers;

        public:
            static ActionDispatcher* getInstance();

           /**
            * Main entry point for scheduling an app action
            */
            void sendMessage(SYSTEM_MESSAGE message);

            void addAuthorizeActionObserver(SystemActions* observer);
            void removeAuthorizeObserver(SystemActions* observer);

            void addRegistrationActionObserver(SystemActions* observer);
            void removeRegistrationObserver(SystemActions* observer);

            void addShowDialogActionObserver(SystemActions* observer);
            void removeShowDialogObserver(SystemActions* observer);

        private:

            void notifyOnAuthorized();
            void notifyOnRegistered();
            void notifyOnShowDialog(int type);
    };

}
