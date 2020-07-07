#include "event_dispatcher.h"

#include <algorithm>

#include "utils/log.h"

using namespace MYLIB;

namespace APP
{

    EventDispatcher* APP::EventDispatcher::getInstance()
    {
        static EventDispatcher eventDispatcher;
        return &eventDispatcher;
    }

    /**
     *  Handle System Event
     */
    void EventDispatcher::sendEvent(EVENT_RESULT event)
    {
        if (event == EVENT_RESULT::AUTHORIZATION_DONE || event == EVENT_RESULT::UNLOCK_DONE)
        {
            Log::Info("EventDispatcher", "sendEvent() - EVENT_RESULT::AUTHORIZATION_DONE \n");

            onAuthorized();
        }

        if (event == EVENT_RESULT::REGISTRATION_DONE)
        {
            Log::Info("EventDispatcher", "sendEvent() - EVENT_RESULT::REGISTRATION_DONE \n");

            onRegistered();
        }

        if (event == EVENT_RESULT ::ACCOUNT_INVALID ||
                event == EVENT_RESULT ::WRONG_PASSWORD ||
                event == EVENT_RESULT ::EMPTY_FIELD ||
                event == EVENT_RESULT ::USER_NAME_EXISTS ||
                event == EVENT_RESULT ::PASSWORD_DIFFERS ||
                event == EVENT_RESULT ::SPACE_CONTAIN ||
                event == EVENT_RESULT::UNLOCK_KEY_INVALID)
        {
            Log::Info("EventDispatcher", "sendEvent() - EVENT_RESULT onShowDialog \n");

            onShowDialog(static_cast<int>(event));
        }

    }

    /**
     * Notify observers
     */
    void EventDispatcher::onAuthorized()
    {
        for (const auto& v : authorization_observers) {
            v->onAuthorized();
        }
    }

    /**
     * Notify observers
     */
    void EventDispatcher::onRegistered()
    {
        for (const auto& v : registration_observers) {
            v->onRegistered();
        }
    }

    /**
     * Notify observers
     */
    void EventDispatcher::onShowDialog(int type)
    {
        for (const auto& v : showdialog_observers) {
            v->onShowDialog(type);
        }
    }

    /**
     * Add observers
     */
    void EventDispatcher::addAuthorizeEventObserver(IAuthorization* observer)
    {
        authorization_observers.push_back(observer);
    }

    void EventDispatcher::removeAuthorizeObserver(IAuthorization* observer)
    {
        auto iterator = std::remove(authorization_observers.begin(), authorization_observers.end(), observer);
        authorization_observers.erase(iterator);
    }

    void EventDispatcher::addRegistrationEventObserver(IRegistration* observer)
    {
        registration_observers.push_back(observer);
    }

    void EventDispatcher::removeRegistrationObserver(IRegistration* observer)
    {
        auto iterator = std::remove(registration_observers.begin(), registration_observers.end(), observer);
        registration_observers.erase(iterator);
    }

    void EventDispatcher::addShowDialogEventObserver(IShowDialog* observer)
    {
        showdialog_observers.push_back(observer);
    }

    void EventDispatcher::removeShowDialogObserver(IShowDialog* observer)
    {
        auto iterator = std::remove(showdialog_observers.begin(), showdialog_observers.end(), observer);
        showdialog_observers.erase(iterator);
    }

    /**
     * Send event
     */
    void sendSystemEvent(EVENT_RESULT event)
    {
        EventDispatcher::getInstance()->sendEvent(event);
    }

}
