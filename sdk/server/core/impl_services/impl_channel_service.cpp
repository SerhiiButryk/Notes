#include "impl_channel_service.h"
#include "../../../concurrency/native_executor.h"
#include "../../net/socket_handler.h"
#include "../../net/connection_receiver.h"
#include "../../common/constants.h"

namespace MYLIB 
{
    namespace SERVER
    {
        static const std::string TAG = "ChannelService";
        
        void ImplChannelService::createChannel() {
            Executor* executor = Executor::getInstance();
            executor->start();

            std::function<TASK_TYPE> _f = openNewChannel;
            executor->schedule(_f);
        }

        void openNewChannel() {
            SocketHandler* socket = SocketHandler::getInstance();
            ConnectionWatcher* watcher = ConnectionWatcher::getInstance();

            try {
                logInfo(TAG, "in openNewChannel - socket created");
                socket->createSocket();

                logInfo(TAG, "in openNewChannel - socket initialized");
                socket->initialize();
                
                logInfo(TAG, "in openNewChannel - socket listen");
                watcher->update(socket->getChannelID(), ConStatus::WAITE_CONNECTION);
                
                logSystem("server listening on " + std::to_string(socket->getChannelID()) + " channel ");
                socket->listen();

                socket->accept();

                logInfo(TAG, "in openNewChannel - socket process client message");

                watcher->update(socket->getChannelID(), ConStatus::CONNECTED);

                logSystem("server accept connection from " + socket->getClientInfo());
                
                socket->serveClient();

                watcher->update(socket->getChannelID(), ConStatus::SESSION_SUCCESSFUL);

            } catch (const LibExcp& e) {
                logError(TAG, std::string("in openNewChannel - ") + e.what());
                
                watcher->update(socket->getChannelID(), ConStatus::SESSION_FAIL);
                return;        
            }
        }

    }

}   