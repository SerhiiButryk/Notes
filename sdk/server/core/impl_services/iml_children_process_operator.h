#ifndef _WAITE_SS_SERVICE_H_
#define _WAITE_SS_SERVICE_H_

#include "../interface/service_waite.h"

namespace MYLIB 
{
    namespace SERVER
    {
        class ImpChildrenProcessOperator : public IWait
        {
            public:
                ImpChildrenProcessOperator() = default;
                ~ImpChildrenProcessOperator() = default;

                void onWait() override; 
        };
        
    }

}

#endif