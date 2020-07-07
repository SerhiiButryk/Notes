#ifndef _OBSERVABLE_SUBJECT_H_
#define _OBSERVABLE_SUBJECT_H_

#include "connection_observer.h"

namespace MYLIB 
{
    class ICSObservable // connection status observer
    {
        public:
            ICSObservable() = default;
            virtual ~ICSObservable() = default;

            virtual void subscribe(ICStatus* ) = 0;
            virtual void unsubscribe(ICStatus* ) = 0;
    };
}

#endif