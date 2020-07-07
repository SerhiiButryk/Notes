#include "socket.h"
#include "constants.h"
#include <sys/types.h>
#include <sys/socket.h>
#include <unistd.h>
#include <netdb.h>
#include <arpa/inet.h>
#include <string.h>
#include <netinet/in.h>
#include "../common/log.h"

namespace MYLIB
{
    Socket::Socket() : _port(DEF_SOCKET_PORT), _ip_address(DEF_IP_LOCAL)
    {
        _is_open = false;
        _fd = 0;
    }
    
    Socket::Socket(int port, std::string ip_address) : _port(port), _ip_address(ip_address)
    {
        _is_open = false;
        _fd = 0;
    }
    
    Socket::~Socket() 
    {
        if (_is_open)
            closeConnection();
    }

    void Socket::open() 
    {
        _fd = socket(AF_INET, SOCK_STREAM, 0);

        if (_fd == -1)
        {
            LOG_ERROR("in Socket::open()","Fail to open socket");
            throw SocketCritical("Fail to open socket");
        }
    }
    
    void Socket::closeConnection() 
    {
        if (!_is_open)
            return;

        close(_fd);

        _is_open = false;
        LOG_INFO("socket","closeConnection()");
    }   
    
    void Socket::connectSock() 
    {
        sockaddr_in hint;
        hint.sin_family = AF_INET;
        hint.sin_port = htons(_port);
        inet_pton(AF_INET, _ip_address.c_str(), &hint.sin_addr);

        int rez = connect(_fd, (sockaddr*) &hint, sizeof(hint));

        if (rez == -1)
        {
            LOG_ERROR("in Socket::connectSock()","Fail to connect socket");
            throw SocketCritical("Fail to connect socket"); 
        }
    }
    
    void Socket::write(const std::string& data) 
    {
        int rez = send(_fd, data.c_str(), data.size() + 1, 0);

        if (rez == -1)
        {
            LOG_INFO("in Socket::senD()","Fail to send data");
            throw SocketWarning("Fail to send data"); 
        }  
    }
    
    std::string Socket::read() 
    {
        char buff[DEF_RCV_BUFF_SZ];
        memset(buff, 0, DEF_RCV_BUFF_SZ);

        int bytes_received = recv(_fd, buff, DEF_RCV_BUFF_SZ, 0);

        if (bytes_received == -1)
        {
            LOG_INFO("in Socket::receive()", "Error receiving data");
            throw SocketWarning("Error receiving data");
        } 

        return std::string(buff, bytes_received);
    }

    int Socket::getPort() const 
    {
        return _port;
    }
    
    std::string Socket::getIP() const 
    {
        return _ip_address;
    }
}