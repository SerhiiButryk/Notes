#pragma once

#include <vector>

#include "utils/jni_wrpper.h"
#include "app/logic/receiver/system_event_receiver.h"
#include "app/logic/base/system_action.h"

namespace APP
{

    /**
     *  This class dispatches the action in response to processed events to registered observers.
     *
     *  It knows about all action.
     *
     *  All requests of any actions should go through this class.
     */

    class ActionDispatcher : private SystemActions
    {
        private:
            std::vector<SystemActions*> authorization_observers;
            std::vector<SystemActions*> registration_observers;
            std::vector<SystemActions*> showdialog_observers;
            std::vector<SystemActions*> unlockkeystore_observers;

    public:
            static ActionDispatcher* getInstance();

            void sendAction(ACTION_TYPE action);

            void addAuthorizeActionObserver(SystemActions* observer);
            void removeAuthorizeObserver(SystemActions* observer);

            void addRegistrationActionObserver(SystemActions* observer);
            void removeRegistrationObserver(SystemActions* observer);

            void addShowDialogActionObserver(SystemActions* observer);
            void removeShowDialogObserver(SystemActions* observer);

            void addUnlockKeystoreActionObserver(SystemActions* observer);
            void removeUnlockKeystoreObserver(SystemActions* observer);

        private:
            /**
             * Native callbacks
             */
            void onAuthorized() override;
            void onRegistered() override;
            void onShowDialog(int type) override;
            void onUnlockKeystore() override;
    };

    /**
     * Helper functions
     */
    void sendSystemAction(ACTION_TYPE action);

}
