#ifndef _IMPL_CHANNEL_SERVICE_H_
#define _IMPL_CHANNEL_SERVICE_H_

#include "../interface/service_channel.h"

namespace MYLIB 
{
    namespace SERVER
    {
        class ImplChannelService : public IChannel
        {
            public:
                ImplChannelService() = default;
                ~ImplChannelService() = default;

                void createChannel() override;
        };
        
        void openNewChannel();
    }
}    

#endif