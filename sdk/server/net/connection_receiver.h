#ifndef _EVENT_RECEIVER_H_
#define _EVENT_RECEIVER_H_

#include <vector>

#include "connection_state.h"
#include "../../common/observable_subject.h"

/*
 *  Observer of registred channel connections
*/

namespace MYLIB
{
    class ConnectionWatcher : public ICSObservable
    {
        private:
            std::vector<ICStatus*> _observers;

            ConnectionWatcher() = default;

        public:
            ~ConnectionWatcher() = default;

            void update(int channel_id, int success_code);
            
            // Observer pattern
            void subscribe(ICStatus* state_update_receiver);
            void unsubscribe(ICStatus* state_update_receiver);
            void unsubscribeAll();
            void notifyAll(int new_state, int channel_id);

            static ConnectionWatcher* getInstance();
    };
}

#endif