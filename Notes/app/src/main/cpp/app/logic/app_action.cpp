#include "app_action.h"

#include "app/logic/action_dispatcher.h"
#include "utils/log.h"
#include "utils/jni_utils.h"

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
    }

    void AppAction::onAuthorized()
    {
        for (auto&& f : authorizeObservers)
        {
            JniUtils::callVoid(f);

            Log::Info(TAG, "notifyOnAuthorized - called observer \n");
        }
    }

    void AppAction::onRegistered()
    {
        if (registerObserver)
        {
            JniUtils::callVoid(*registerObserver);

            Log::Info(TAG, "notifyOnRegistered - called observer \n");
        } else {
            Log::Error(TAG, "notifyOnRegistered - no observer \n");
        }
    }

    void AppAction::onShowDialog(int type)
    {
        if (showDialogObserver)
        {
            JniUtils::callVoid(*showDialogObserver, type);

            Log::Info(TAG, "notifyOnShowDialog - called observer \n");
        } else {
            Log::Error(TAG, "notifyOnShowDialog - no observer \n");
        }
    }

    void AppAction::setAuthorizeCallback(JNIWrapper authorize_callback)
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

}