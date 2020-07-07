#ifndef _SERVICE_CHANNEL_H_
#define _SERVICE_CHANNEL_H_

namespace MYLIB 
{
    namespace SERVER
    {
        class IChannel
        {
            public:
                IChannel() = default;
                virtual ~IChannel() = default;

                virtual void createChannel() = 0;
        };
    }
}

#endif

