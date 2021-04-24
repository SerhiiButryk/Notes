#pragma once

#include <vector>

#include "utils/jni_wrpper.h"
#include "app/logic/base/system_action.h"

#include <memory>

using namespace MYLIB;

namespace APP
{

    /**
     *  This class is a concrete action handler
     *
     *  It performs notification calls to Java side.
     *
     *  Note: This class is an observer of the events in ActionDispatcher class.
     */

    class AppAction : public SystemActions
    {
        private:

            std::vector<JNIWrapper> authorizeObservers;
            std::unique_ptr<JNIWrapper> showDialogObserver;
            std::unique_ptr<JNIWrapper> registerObserver;

        public:

            static AppAction* getInstance();

            void setAuthorizeCallback(JNIWrapper authorize_callback);
            void setShowDialogCallback(JNIWrapper* showdialog_callback);
            void setRegistrationCallback(JNIWrapper* registration_callback);

            void onAuthorized() override;
            void onRegistered() override;
            void onShowDialog(int type) override;

        private:
            AppAction();
    };

}
