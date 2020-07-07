#include "server_manager.h"

static const std::string TAG = "ServerManager";

namespace MYLIB 
{
    ServerManager::ServerManager(IExit& s1, 
        ChannelService& s2, InitService& s3, DestroyService& s4, WaitService& s5) 
        : _exit_service(s1), _channel_service(s2), _init_service(s3), 
        _destroy_service(s4), _wait_service(s5) {} 

    void ServerManager::initManager() {
        _init_service.initService();
    }

    void ServerManager::createServerChannel() {
        _channel_service.createChannel();
    }

    void ServerManager::onWaitAllProcesses() {
       _wait_service.onWait();
    }

    void ServerManager::prepareServerExit() {
        _exit_service.shutdown();
    }
    
    void ServerManager::shutdown() {
        _destroy_service.destroyService();
    }
}
