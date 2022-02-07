#pragma once

namespace APP
{
    /**
     *  Interface which describes the system actions.
     *
     *  Implement it to receive a notification about the next action:
     *
     *  1. User authorized event.
     *  2. Dialog displaying event.
     *  3. User registered event.
     *
     */

    class SystemActions
    {
        public:
            virtual ~SystemActions() {}

            virtual void onAuthorized() = 0;

            virtual void onShowDialog(int type) = 0;

            virtual void onRegistered() = 0;
    };

}
