#pragma once

#include <string>

#include "app_common/types.h"

namespace APP
{
    /**
     *  TODO: Implement synchronization with server
     */

    class ServerAgent
    {
        private:

        public:
            void sendAuthorizeMessage(const std::string& userID);
    };

}
