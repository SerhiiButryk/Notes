#include "action_dispatcher.h"

#include <algorithm>
#include <app/core/app_action_sender.h>

#include "utils/log.h"

using namespace MYLIB;

namespace APP
{

    ActionDispatcher* APP::ActionDispatcher::getInstance()
    {
        static ActionDispatcher eventDispatcher;
        return &eventDispatcher;
    }

    /**
     *  Handle System Event
     */
    void ActionDispatcher::sendAction(ACTION_TYPE action)
    {
        if (action == ACTION_TYPE::AUTHORIZATION_DONE || action == ACTION_TYPE::UNLOCK_DONE)
        {
            Log::Info("ActionDispatcher", "sendEvent() - ACTION::AUTHORIZATION_DONE \n");

            onAuthorized();
        }

        if (action == ACTION_TYPE::REGISTRATION_DONE)
        {
            Log::Info("ActionDispatcher", "sendEvent() - ACTION::REGISTRATION_DONE \n");

            onRegistered();
        }

        if (action == ACTION_TYPE ::ACCOUNT_INVALID ||
            action == ACTION_TYPE ::WRONG_PASSWORD ||
            action == ACTION_TYPE ::EMPTY_FIELD ||
            action == ACTION_TYPE ::USER_NAME_EXISTS ||
            action == ACTION_TYPE ::PASSWORD_DIFFERS ||
            action == ACTION_TYPE ::SPACE_CONTAIN ||
            action == ACTION_TYPE::UNLOCK_KEY_INVALID)
        {
            Log::Info("ActionDispatcher", "sendEvent() - ACTION onShowDialog \n");

            onShowDialog(static_cast<int>(action));
        }

        if (action == ACTION_TYPE::UNLOCK_KEYSTORE)
        {
            Log::Info("ActionDispatcher", "sendEvent() - ACTION onUnlockKeystore \n");

            onUnlockKeystore();
        }

    }

    /**
     * Notify observers
     */
    void ActionDispatcher::onAuthorized()
    {
        for (const auto& v : authorization_observers) {
            v->onAuthorized();
        }
    }

    /**
     * Notify observers
     */
    void ActionDispatcher::onRegistered()
    {
        for (const auto& v : registration_observers) {
            v->onRegistered();
        }
    }

    /**
     * Notify observers
     */
    void ActionDispatcher::onShowDialog(int type)
    {
        for (const auto& v : showdialog_observers) {
            v->onShowDialog(type);
        }
    }

    /**
     * Notify observers
     */
    void ActionDispatcher::onUnlockKeystore()
    {
        for (const auto& v : unlockkeystore_observers) {
            v->onUnlockKeystore();
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

    void ActionDispatcher::addUnlockKeystoreActionObserver(SystemActions* observer)
    {
        unlockkeystore_observers.push_back(observer);
    }

    void ActionDispatcher::removeUnlockKeystoreObserver(SystemActions* observer)
    {
        auto iterator = std::remove(unlockkeystore_observers.begin(), unlockkeystore_observers.end(), observer);
        unlockkeystore_observers.erase(iterator);
    }

    /**
     * Send event
     */
    void sendSystemAction(ACTION_TYPE action)
    {
        ActionDispatcher::getInstance()->sendAction(action);
    }

}
