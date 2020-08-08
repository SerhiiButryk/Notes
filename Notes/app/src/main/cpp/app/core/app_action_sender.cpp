#include "app_action_sender.h"

#include "app/logic/action_dispatcher.h"
#include "utils/log.h"

namespace {
    const std::string TAG = "AppActionSender";
}

namespace APP
{

    AppActionSender* AppActionSender::getInstance()
    {
        static AppActionSender event;
        return &event;
    }

    AppActionSender::AppActionSender()
    {
        // Register this handler to corresponding actions
        ActionDispatcher::getInstance()->addAuthorizeActionObserver(this);
        ActionDispatcher::getInstance()->addRegistrationActionObserver(this);
        ActionDispatcher::getInstance()->addShowDialogActionObserver(this);
        ActionDispatcher::getInstance()->addUnlockKeystoreActionObserver(this);
    }

    void AppActionSender::onAuthorized()
    {
        for (auto&& f : authorizeObservers)
        {
            callVoid(f);
        }
    }

    void AppActionSender::onRegistered()
    {
        if (registerObserver)
        {
            callVoid(*registerObserver);
        }
    }

    void AppActionSender::onShowDialog(int type)
    {
        if (showDialogObserver)
        {
            callVoid(*showDialogObserver, type);
        }
    }

    void AppActionSender::onUnlockKeystore()
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

    void AppActionSender::addAuthorizeCallback(JNIWrapper authorize_callback)
    {
        authorizeObservers.push_back(std::move(authorize_callback));
    }

    void AppActionSender::setShowDialogCallback(JNIWrapper* showdialog_callback)
    {
        showDialogObserver.reset(showdialog_callback);
    }

    void AppActionSender::setRegistrationCallback(JNIWrapper* registration_callback)
    {
        registerObserver.reset(registration_callback);
    }

    void AppActionSender::setUnlockKeystoreNoteViewCallback(JNIWrapper* unlock_keystore_callback)
    {
        unlockKeystoreObserverNoteViewActivity.reset(unlock_keystore_callback);
    }

    void AppActionSender::setUnlockKeystoreEditorViewCallback(JNIWrapper* unlock_keystore_callback)
    {
        unlockKeystoreObserverNoteEditorActivity.reset(unlock_keystore_callback);
    }

    void AppActionSender::removeUnlockKeystoreNoteViewCallback()
    {
        unlockKeystoreObserverNoteViewActivity.reset(nullptr);
    }

    void AppActionSender::removeUnlockKeystoreEditorViewCallback()
    {
        unlockKeystoreObserverNoteEditorActivity.reset(nullptr);
    }


}