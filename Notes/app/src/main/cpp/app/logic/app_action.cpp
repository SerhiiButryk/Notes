#include "app_action.h"

#include "app/logic/action_dispatcher.h"
#include "utils/log.h"
#include "utils/jni_utils.h"


static const char* TAG = "AppActionSender";


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

            Info(TAG, "notifyOnAuthorized - called observer \n");
        }
    }

    void AppAction::onRegistered()
    {
        if (registerObserver)
        {
            JniUtils::callVoid(*registerObserver);

            Info(TAG, "notifyOnRegistered - called observer \n");
        } else {
            Error(TAG, "notifyOnRegistered - no observer \n");
        }
    }

    void AppAction::onShowDialog(int type)
    {
        if (showDialogObserver)
        {
            JniUtils::callVoid(*showDialogObserver, type);

            Info(TAG, "notifyOnShowDialog - called observer \n");
        } else {
            Error(TAG, "notifyOnShowDialog - no observer \n");
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