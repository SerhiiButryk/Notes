#ifndef _SERVER_MANAGER_H_
#define _SERVER_MANAGER_H_

#include <string>

#include "impl_services/impl_channel_service.h"
#include "impl_services/impl_exit_service.h"
#include "impl_services/impl_resource_service.h"
#include "impl_services/iml_children_process_operator.h"

/**
 * This class is responsible for the properly server startup and finilaze logic.
 * Also it creates background executions as needed for client connection listening.
*/

using namespace MYLIB::SERVER;

namespace MYLIB
{
    class ServerManager
    {
        private:
            IExit& _exit_service;
            ChannelService& _channel_service;
            WaitService& _wait_service;
            InitService& _init_service;
            DestroyService& _destroy_service;

        public:
            ServerManager(IExit& s1, 
                          ChannelService& s2,
                          InitService& s3,
                          DestroyService& s4,
                          WaitService& s5);

            ~ServerManager() = default;

            void initManager();
            void createServerChannel();
            void onWaitAllProcesses();
            void prepareServerExit();
            void shutdown();
    };
}

#endif