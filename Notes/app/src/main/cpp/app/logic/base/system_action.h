#pragma once

namespace APP
{
    /**
     *  Interface class of system actions
     *
     *  Extends it to add a new action
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
