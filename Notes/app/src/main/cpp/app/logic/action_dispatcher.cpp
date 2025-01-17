#include "action_dispatcher.h"

#include <algorithm>
#include <app/logic/app_action.h>

#include "utils/log.h"

using namespace MYLIB;

namespace APP
{

    ActionDispatcher* APP::ActionDispatcher::getInstance()
    {
        static ActionDispatcher eventDispatcher;
        return &eventDispatcher;
    }

    void ActionDispatcher::sendMessage(SYSTEM_MESSAGE message)
    {
        if (message == SYSTEM_MESSAGE::AUTHORIZATION_DONE || message == SYSTEM_MESSAGE::UNLOCK_DONE)
        {
            Info("ActionDispatcher", "sendEvent() - ACTION::AUTHORIZATION_DONE \n");

            notifyOnAuthorized();
            return;
        }

        if (message == SYSTEM_MESSAGE::REGISTRATION_DONE)
        {
            Info("ActionDispatcher", "sendEvent() - ACTION::REGISTRATION_DONE \n");

            notifyOnRegistered();
            return;
        }

        if (message == SYSTEM_MESSAGE ::ACCOUNT_INVALID ||
            message == SYSTEM_MESSAGE ::WRONG_PASSWORD ||
            message == SYSTEM_MESSAGE ::EMPTY_FIELD ||
            message == SYSTEM_MESSAGE ::USER_NAME_EXISTS ||
            message == SYSTEM_MESSAGE ::PASSWORD_DIFFERS ||
            message == SYSTEM_MESSAGE ::SPACE_CONTAIN ||
            message == SYSTEM_MESSAGE ::UNLOCK_KEY_INVALID ||
            message == SYSTEM_MESSAGE ::PASSWORD_IS_WEAK ||
            message == SYSTEM_MESSAGE ::EMAIL_INVALID)
        {
            Info("ActionDispatcher", "sendEvent() - ACTION notifyOnShowDialog \n");

            notifyOnShowDialog(static_cast<int>(message));
            return;
        }

        Error("ActionDispatcher", "sendEvent() - NO ACTION FOUND\n");

    }

    /**
     * Notify observers
     */
    void ActionDispatcher::notifyOnAuthorized()
    {
        for (const auto& v : authorization_observers) {
            v->onAuthorized();
        }
    }

    /**
     * Notify observers
     */
    void ActionDispatcher::notifyOnRegistered()
    {
        for (const auto& v : registration_observers) {
            v->onRegistered();
        }
    }

    /**
     * Notify observers
     */
    void ActionDispatcher::notifyOnShowDialog(int type)
    {
        for (const auto& v : showdialog_observers) {
            v->onShowDialog(type);
        }
    }

    /**
     * Add observers
     */
    void ActionDispatcher::addAuthorizeActionObserver(SystemActions* observer)
    {
        authorization_observers.push_back(observer);
    }

    void ActionDispatcher::removeAuthorizeObserver(SystemActions* observer)
    {
        auto iterator = std::remove(authorization_observers.begin(), authorization_observers.end(), observer);
        authorization_observers.erase(iterator);
    }

    void ActionDispatcher::addRegistrationActionObserver(SystemActions* observer)
    {
        registration_observers.push_back(observer);
    }

    void ActionDispatcher::removeRegistrationObserver(SystemActions* observer)
    {
        auto iterator = std::remove(registration_observers.begin(), registration_observers.end(), observer);
        registration_observers.erase(iterator);
    }

    void ActionDispatcher::addShowDialogActionObserver(SystemActions* observer)
    {
        showdialog_observers.push_back(observer);
    }

    void ActionDispatcher::removeShowDialogObserver(SystemActions* observer)
    {
        auto iterator = std::remove(showdialog_observers.begin(), showdialog_observers.end(), observer);
        showdialog_observers.erase(iterator);
    }

}
