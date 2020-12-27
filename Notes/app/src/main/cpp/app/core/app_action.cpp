#include "app_action.h"

#include "app/logic/action_dispatcher.h"
#include "utils/log.h"

namespace {
    const std::string TAG = "AppActionSender";
}

namespace APP
{

    AppAction* AppAction::getInstance()
    {
        static AppAction event;
        return &event;
    }

    AppAction::AppAction()
    {
        // Register this handler to corresponding actions
        ActionDispatcher::getInstance()->addAuthorizeActionObserver(this);
        ActionDispatcher::getInstance()->addRegistrationActionObserver(this);
        ActionDispatcher::getInstance()->addShowDialogActionObserver(this);
        ActionDispatcher::getInstance()->addUnlockKeystoreActionObserver(this);
    }

    void AppAction::onAuthorized()
    {
        for (auto&& f : authorizeObservers)
        {
            callVoid(f);

            Log::Info(TAG, "onAuthorized - called observer \n");
        }
    }

    void AppAction::onRegistered()
    {
        if (registerObserver)
        {
            callVoid(*registerObserver);

            Log::Info(TAG, "onRegistered - called observer \n");
        } else {
            Log::Error(TAG, "onRegistered - no observer \n");
        }
    }

    void AppAction::onShowDialog(int type)
    {
        if (showDialogObserver)
        {
            callVoid(*showDialogObserver, type);

            Log::Info(TAG, "onShowDialog - called observer \n");
        } else {
            Log::Error(TAG, "onShowDialog - no observer \n");
        }
    }

    void AppAction::onUnlockKeystore()
    {
        if (unlockKeystoreObserverNoteViewActivity)
        {
            Log::Info(TAG, "onUnlockKeystore - unlockKeystoreObserverNoteViewActivity \n");

            callVoid(*unlockKeystoreObserverNoteViewActivity);
        }

        if (unlockKeystoreObserverNoteEditorActivity)
        {
            Log::Info(TAG, "onUnlockKeystore - unlockKeystoreObserverNoteEditorActivity \n");

            callVoid(*unlockKeystoreObserverNoteEditorActivity);
        }
    }

    void AppAction::addAuthorizeCallback(JNIWrapper authorize_callback)
    {
        authorizeObservers.push_back(std::move(authorize_callback));
    }

    void AppAction::setShowDialogCallback(JNIWrapper* showdialog_callback)
    {
        showDialogObserver.reset(showdialog_callback);
    }

    void AppAction::setRegistrationCallback(JNIWrapper* registration_callback)
    {
        registerObserver.reset(registration_callback);
    }

    void AppAction::setUnlockKeystoreNoteViewCallback(JNIWrapper* unlock_keystore_callback)
    {
        unlockKeystoreObserverNoteViewActivity.reset(unlock_keystore_callback);
    }

    void AppAction::setUnlockKeystoreEditorViewCallback(JNIWrapper* unlock_keystore_callback)
    {
        unlockKeystoreObserverNoteEditorActivity.reset(unlock_keystore_callback);
    }

    void AppAction::removeUnlockKeystoreNoteViewCallback()
    {
        unlockKeystoreObserverNoteViewActivity.reset(nullptr);
    }

    void AppAction::removeUnlockKeystoreEditorViewCallback()
    {
        unlockKeystoreObserverNoteEditorActivity.reset(nullptr);
    }


}