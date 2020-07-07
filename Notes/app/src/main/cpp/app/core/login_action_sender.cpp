#include "login_action_sender.h"

namespace APP
{

    LoginActionSender* LoginActionSender::getInstance()
    {
        static LoginActionSender event;
        return &event;
    }

    void LoginActionSender::onAuthorized()
    {
        for (auto&& f : authorizeObservers)
        {
            callVoid(f);
        }
    }

    void LoginActionSender::onRegistered()
    {
        for (auto&& f : registerObservers)
        {
            callVoid(f);
        }
    }

    void LoginActionSender::onShowDialog(int type)
    {
        for (auto&& f : showDialogObservers)
        {
            callVoid(f, type);
        }

    }

    void LoginActionSender::addAuthorizeCallback(JNIWrapper authorize_callback)
    {
        authorizeObservers.push_back(std::move(authorize_callback));
    }

    void LoginActionSender::addShowDialogCallback(JNIWrapper showdialog_callback)
    {
        showDialogObservers.push_back(std::move(showdialog_callback));
    }

    void LoginActionSender::addRegistrationCallback(JNIWrapper registration_callback)
    {
        registerObservers.push_back(std::move(registration_callback));
    }

}