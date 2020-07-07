#ifndef _SERVER_SOCKET_H_
#define _SERVER_SOCKET_H_

#include <string>
#include <netinet/in.h>

#include "../../common/exception.h"

/*
*   Server side socket implementation
*/

namespace MYLIB
{
    namespace SERVER
    {
        class Socket
        {
            private:
                const int _port;
                const std::string _ip_address;
                std::string _cln_host;
                int _cln_service;
                bool _is_open_lsn_socket;
                bool _is_open_cln_socket;
                int _fd_lsn;
                int _fd_cln;
                sockaddr_in _cln_addr;

                void init();
                void retrieveClnInfo();

            public:

                Socket();
                Socket(int port, std::string ip_address);
                Socket(const Socket&) = delete;
                Socket(Socket&&) = default;
                ~Socket();

                Socket& operator=(const Socket& ) = delete;
                Socket& operator=(Socket&&) = default;

                void open();
                void attach();
                void waitClient();
                void acceptClient();
                std::string read();
                void write(const std::string& data);

                void closeLsnSocket();
                void closeClnSocket();

                std::string getClnIP() const;
                std::string getIP() const;
                int getClnPort() const;
                int getPort() const;
        };

        class SocketCritical : public CriticalExp
        {
            public:
                SocketCritical(const std::string& mes);
        };

        class SocketWarning : public WarningExp
        {
            public:
                SocketWarning(const std::string& mes);
        };

    }
}

#endif
