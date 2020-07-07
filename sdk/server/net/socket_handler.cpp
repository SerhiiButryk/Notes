#include "socket_handler.h"

#include <iostream>
#include <sstream>
#include <cstring>

#include "../../common/log.h"

static std::string TAG = "SocketHandler";

namespace MYLIB 
{
    SocketHandler::SocketHandler() : _state(INVALIDE), _socket(nullptr), _channel_id(0) {
    }

    SocketHandler::~SocketHandler() {
        if (_socket) {
            delete _socket;
        }
    }

    void SocketHandler::createSocket(int port, const std::string& address) {
        _socket = new Socket();
        _state = CREATED;
    }

    void SocketHandler::initialize() {
        if (isNotState(CREATED)) {
            throw SocketInvalidStateException("in SocketHandler::initialize - Socket was not created");    
        }

        try {
            
            logInfo(TAG, "Socket is opened");
            _socket->open();
            
            logInfo(TAG, "Socket is bound");
            _socket->attach();

        } catch (const SocketCritical& e) {
            logInfo(TAG, e.what());
            throw e;
        }

        _state = INITIALIZED;
    }

    void SocketHandler::listen() {
        if (isNotState(INITIALIZED)) {
            throw SocketInvalidStateException("in SocketHandler::listen - Socket was not initialized");    
        }    

        _state = LISTENING;

        logInfo(TAG, "Socket is listening");
        _socket->waitClient();
    }

    void SocketHandler::accept() {
        if (isNotState(LISTENING)) {
            throw SocketInvalidStateException("in SocketHandler::accept - Socket was not in listening state before");    
        }

        logInfo(TAG, "Socket accept client");
        _socket->acceptClient();

        _state = ACCEPTED;
    }

    void SocketHandler::serveClient() {
        if (isNotState(ACCEPTED)) {
            throw SocketInvalidStateException("in SocketHandler::serveClient - client was not accepted");    
        }

        _state  = EXCHANGE;

        while (true)
        {
            logInfo(TAG, "server try to read a message");

            std::string data;    
            
            try {

                data = _socket->read();

            } catch(const SocketWarning& e) { 
                logInfo(TAG, e.what());
                throw e;
            }

            if (strcasecmp(data.c_str(), "end") == 0)
            {
                try {

                    _socket->write("Server end");

                } catch(const SocketWarning& e) {
                    logInfo(TAG, e.what());
                    throw e;
                }
                
                break;
            }

            // Display received message
            stdOut("Received:", data);
            
            try {

                logInfo(TAG, "server try to send a message");

                _socket->write(data);

            } catch(const SocketWarning& e) {
                logInfo(TAG, e.what());
                throw e;
            }
        }
    }

    bool SocketHandler::handleException(const LibExcp& e) {
        int code = e.getErrorCode();

        std::stringstream ss;
        ss << std::to_string(code) << " ";

        // We can not proceed next
        if (isCritical(code)) {
            ss << "CRITICAL" << "\n";
            ss << e.what();
        
            logError(TAG, ss.str());
        
            return false;
        }

        ss << "WARNING" << "\n";
        ss << e.what();

        logError(TAG, ss.str());

        // Ok can proceed
        return true;
    }

    void SocketHandler::setChannelID(int id) { _channel_id = id; }
    
    int SocketHandler::getChannelID() { return _channel_id; }

    bool SocketHandler::isNotState(int state) {
        return state != _state;
    }

    SocketHandler* SocketHandler::getInstance() {
        static SocketHandler handler;
        return &handler;
    }

    std::string SocketHandler::getClientInfo() {
        std::stringstream ss;
        ss << _socket->getClnIP() <<" connected on " << _socket->getClnPort() ;
        return ss.str();
    }

    int SocketHandler::getCurrentState() {
        return _state;
    }

    // --- Exceptions -----
    SocketInvalidStateException::SocketInvalidStateException(const std::string& message) 
        : CriticalExp(message) {}
}    