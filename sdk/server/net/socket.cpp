#include "socket.h"

#include <sys/types.h>
#include <sys/socket.h>
#include <unistd.h>
#include <netdb.h>
#include <arpa/inet.h>
#include <string.h>

#include "../../net/constants.h"
#include "../../common/log.h"

namespace MYLIB
{
    namespace SERVER 
    {
        static const std::string TAG = "ServerSocket";

        Socket::Socket() : _port(DEF_SERVER_PORT), _ip_address(ANY_ADDRESS)
        {
            init();
        }

        Socket::Socket(int port, std::string ip_address) : _port(port), _ip_address(ip_address)
        {
            init();
        }

        void Socket::init()
        {
            _is_open_lsn_socket = false;
            _is_open_cln_socket = false;
            _fd_lsn = 0;
            _fd_lsn = 0;
            _cln_service = 0;
        }

        Socket::~Socket()
        {
            if (_is_open_lsn_socket)
                closeLsnSocket();

            if (_is_open_cln_socket)
                closeClnSocket();
        }

        void Socket::open()
        {
            _fd_lsn = socket(AF_INET, SOCK_STREAM, 0);

            if (_fd_lsn == -1) {
                logError(TAG,"Fail to create a listening socket");
                throw SocketCritical("Fail to create a listening socket");
            }

            _is_open_lsn_socket = true;
        }

        void Socket::attach()
        {
            // Bind a socket to IP port
            sockaddr_in hint;
            hint.sin_family = AF_INET;
            hint.sin_port = htons(_port); // host to net short address
            hint.sin_addr.s_addr = htonl(INADDR_ANY);
            //hint.sin_addr.s_addr = INADDR_ANY;

            int rez = bind(_fd_lsn, (struct sockaddr* ) &hint, sizeof(hint));

            if (rez == -1) {
                logError(TAG,"Fail to bind listening socket");
                throw SocketCritical("Fail to bind listening socket");
            }

            logInfo(TAG,"Socket is bound");
        }

        void Socket::waitClient()
        {
            // Mark a socket as listening
            int rez = listen(_fd_lsn, SOMAXCONN);

            if (rez == -1) {
                logError(TAG,"Fail to start listening");
                throw SocketCritical("Fail to start listening");
            }
        }

        void Socket::acceptClient()
        {
            socklen_t sz = sizeof(_cln_addr);

            char host[NI_MAXHOST];
            char service[NI_MAXSERV];

            _fd_cln = accept(_fd_lsn, (struct sockaddr*) &_cln_addr, &sz);

            if (_fd_cln == -1) {
                logInfo(TAG,"Failed accept a client connection");
                throw SocketWarning("Failed accept a client connection");
            }

            _is_open_cln_socket = true;

            retrieveClnInfo();
        }

        std::string Socket::read()
        {
            char buff[DEF_RCV_BUFF_SZ];
            memset(buff, 0, DEF_RCV_BUFF_SZ);

            int bytes_received = recv(_fd_cln, buff, DEF_RCV_BUFF_SZ, 0);

            if (bytes_received == -1) {
                logInfo(TAG,"Connection errors");
                throw SocketWarning("Connection errors");
            }

            if (bytes_received == 0) {
                logInfo(TAG,"A client is disconnected");
                throw SocketWarning("A client is disconnected");
            }

            return std::string(buff, bytes_received);
        }

        void Socket::write(const std::string& data)
        {
            int rez = send(_fd_cln, data.c_str(), data.size() + 1, 0);

            if (rez == -1) {
                logInfo(TAG,"Sending data error");
                throw SocketWarning("Sending data error");
            }
        }

        void Socket::retrieveClnInfo()
        {
            socklen_t sz = sizeof(_cln_addr);

            char host[NI_MAXHOST];
            char service[NI_MAXSERV];

            memset(host, 0, NI_MAXHOST);
            memset(service, 0, NI_MAXSERV);

            int rez = getnameinfo((sockaddr*) &_cln_addr, sz, host,
                                NI_MAXHOST, service, NI_MAXSERV, 0 );

            if (rez) {
                std::string tmp(service, NI_MAXSERV);
                _cln_service = atoi(tmp.c_str());
            } else {
                // convert back to the redable array
                inet_ntop(AF_INET, &_cln_addr.sin_addr, host, NI_MAXHOST);
                _cln_service = ntohs(_cln_addr.sin_port);
            }

            _cln_host.assign(host, NI_MAXHOST);
        }

        void Socket::closeLsnSocket()
        {
            close(_fd_lsn);
            _is_open_lsn_socket = false;
            logInfo(TAG,"closeLsnSocket()");
        }

        void Socket::closeClnSocket()
        {
            close(_fd_cln);
            _is_open_cln_socket = false;
            logInfo(TAG,"closeClnSocket()");
        }

        std::string Socket::getClnIP() const
        {
            return _cln_host;
        }

        int Socket::getClnPort() const
        {
            return _cln_service;
        }

        std::string Socket::getIP() const
        {
            return _ip_address;
        }

        int Socket::getPort() const
        {
            return _port;
        }

        // --- Exceptions ----- //
        SocketCritical::SocketCritical(const std::string& message) : CriticalExp(message) {}

        SocketWarning::SocketWarning(const std::string& message) : WarningExp(message) {}

    }

}
