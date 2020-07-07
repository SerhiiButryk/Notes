#ifndef _SERVICE_WAIT_H_
#define _SERVICE_WAIT_H_

namespace MYLIB 
{
    namespace SERVER
    {
        class IWait
        {
            public:
                IWait() = default;
                virtual ~IWait() = default;

                virtual void onWait() = 0; 
        };
    }
}

#endif