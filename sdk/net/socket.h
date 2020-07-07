#ifndef _SOCKET_H_
#define _SOCKET_H_

#include <string>

#include "../common/exception.h"

/**
*   Client side implementation
*/

namespace MYLIB
{
    class Socket
    {
        private:
            const int _port;
            const std::string _ip_address;
            bool _is_open;
            int _fd;

        public:
            Socket();
            Socket(int port, std::string ip_address);
            ~Socket();

            Socket(const Socket&) = delete;
            Socket& operator=(const Socket& ) = delete;

            void open();
            void connectSock();
            void write(const std::string& data);
            std::string read();
            void closeConnection();

            int getPort() const;
            std::string getIP() const;
    };

    class SocketCritical : public CriticalExp {
        public:
            SocketCritical(std::string mes) : CriticalExp(mes) {}
    };

    class SocketWarning : public WarningExp {
        public:
            SocketWarning(std::string mes) : WarningExp(mes) {}
    }; 
    
} 

#endif