#ifndef _SERVER_H_
#define _SERVER_H_

#include "socket.h"

#include <string>

#include "../../net/constants.h"
#include "../../common/exception.h"

/**
 *  Represent a single server socket connection endpoint
*/

using namespace MYLIB::SERVER;

namespace MYLIB 
{
    class SocketHandler 
    {
        private:

            enum STATE {
                CREATED, 
                INITIALIZED,
                LISTENING,
                ACCEPTED,
                EXCHANGE,
                INVALIDE
            }; 
            
            STATE _state;
            SERVER::Socket* _socket;
            int _channel_id;

            SocketHandler();

            bool isNotState(int state);

        public:
            ~SocketHandler();

            static SocketHandler* getInstance();

            void createSocket(int port = DEF_SERVER_PORT, const std::string& local_address = DEF_IP_LOCAL);
            void initialize();   
            void listen();
            void accept();
            void serveClient();

            std::string getClientInfo();
            int getCurrentState();
            
            void setChannelID(int id);
            int getChannelID();

            bool handleException(const LibExcp& exception);
    };

    class SocketInvalidStateException : public CriticalExp
    {
        public:
            SocketInvalidStateException(const std::string& message);
    };
}

#endif