#pragma once

#include <vector>

#include "utils/jni_wrpper.h"
#include "app/logic/system/action_interface.h"

using namespace MYLIB;

namespace APP
{

    /**
     *  Sender for UI actions.
     *
     *  This class performs actual calls to Java side.
     *
     *  Note: This class is an observer of the events in EventDispatcher class.
     */

    class LoginActionSender : public IAuthorization, public IRegistration, public IShowDialog
    {
        private:

            std::vector<JNIWrapper> authorizeObservers;
            std::vector<JNIWrapper> showDialogObservers;
            std::vector<JNIWrapper> registerObservers;

        public:

            static LoginActionSender* getInstance();

            void addAuthorizeCallback(JNIWrapper authorize_callback);
            void addShowDialogCallback(JNIWrapper showdialog_callback);
            void addRegistrationCallback(JNIWrapper registration_callback);

            void onAuthorized() override;
            void onRegistered() override;
            void onShowDialog(int type) override;
    };

}
