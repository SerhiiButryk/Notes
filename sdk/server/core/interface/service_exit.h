#ifndef _SERVICE_EXIT_H_
#define _SERVICE_EXIT_H_

namespace MYLIB 
{
    namespace SERVER
    {
        class IExit
        {
            public:
                IExit() = default;
                virtual ~IExit() = default;

                virtual void shutdown() = 0;
        };
    }
}

#endif