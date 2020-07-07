#ifndef _IMPL_EXIT_SERVICE_H_
#define _IMPL_EXIT_SERVICE_H_

#include "../interface/service_exit.h"

namespace MYLIB 
{
    namespace SERVER
    {
        class ImplExitService : public IExit
        {
            public:
                ImplExitService() = default;
                ~ImplExitService() = default;

                void shutdown() override;
        };
        
    }

}

#endif