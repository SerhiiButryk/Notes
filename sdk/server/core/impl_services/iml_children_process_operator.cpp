#include "iml_children_process_operator.h"

#include <string>
#include <chrono>
#include <thread>

#include "../../../common/log.h"
#include "../registrat.h"
#include "../../net/socket_handler.h"

namespace MYLIB 
{
    namespace SERVER
    {
        void ImpChildrenProcessOperator::onWait() 
        {
            logSystem("main thread is waiting");

            int _id = SocketHandler::getInstance()->getChannelID();

            const ConnectionState& current = NetConnection::getInstance()->getConnectionStatus(_id);

            while (! (current.isInStatus(ConStatus::SESSION_FAIL) || current.isInStatus(ConStatus::SESSION_SUCCESSFUL)) )
            {
                std::this_thread::sleep_for(std::chrono::seconds(5));
                logSystem("server is idle...");
            }

            if (current.isInStatus(ConStatus::SESSION_SUCCESSFUL)) 
            {
                logSystem("server is complete current job, result = success");
            } else {
                logSystem("server is complete current job, result = FAIL");
            }        
        }
    }

}