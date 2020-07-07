#pragma once

namespace APP
{
    /**
     *  Interface classes of system actions.
     *
     *  Class which implements it and register them selfs as observer of if
     *  will be notified of this events.
     *
     *  Classes should be registered in EventDispatcher class.
     */

    class IAuthorization
    {
        public:
            virtual ~IAuthorization() {}

            virtual void onAuthorized() = 0;
    };

    class IRegistration
    {
        public:
            virtual ~IRegistration() {}

            virtual void onRegistered() = 0;
    };

    class IShowDialog
    {
        public:
            virtual ~IShowDialog() {}

            virtual void onShowDialog(int type) = 0;
    };
}
