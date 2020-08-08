#pragma once

#include <app_common/types.h>
#include "app/logic/base/event.h"

namespace APP
{

    class AuthorizeEvent : public Event<SYSTEM_EVENT>
    {
        public:
            AuthorizeEvent();
    };

}
