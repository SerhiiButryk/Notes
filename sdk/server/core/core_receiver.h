#ifndef _CORE_RECEIVER_H_
#define _CORE_RECEIVER_H_

#include "../../common/observable_subject.h"

namespace MYLIB 
{
    class CoreReceiver : public ICSObservable
    {
        private:

        public:
            CoreReceiver();
            ~CoreReceiver();

            void notify(int new_state);
    };
    
}

#endif