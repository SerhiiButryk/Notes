#pragma once

#include <vector>

#include "utils/jni_wrpper.h"
#include "app/logic/base/system_action.h"

#include <memory>

using namespace MYLIB;

namespace APP
{

    /**
     *  This class is specific action handler
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

            std::unique_ptr<JNIWrapper> unlockKeystoreObserverNoteViewActivity;
            std::unique_ptr<JNIWrapper> unlockKeystoreObserverNoteEditorActivity;

        public:

            static AppAction* getInstance();

            void setAuthorizeCallback(JNIWrapper authorize_callback);
            void setShowDialogCallback(JNIWrapper* showdialog_callback);
            void setRegistrationCallback(JNIWrapper* registration_callback);

            void setUnlockKeystoreNoteViewCallback(JNIWrapper* unlock_keystore_callback);
            void removeUnlockKeystoreNoteViewCallback();

            void setUnlockKeystoreEditorViewCallback(JNIWrapper* unlock_keystore_callback);
            void removeUnlockKeystoreEditorViewCallback();

            void onAuthorized() override;
            void onRegistered() override;
            void onShowDialog(int type) override;
            void onUnlockKeystore() override;

        private:
            AppAction();
    };

}
