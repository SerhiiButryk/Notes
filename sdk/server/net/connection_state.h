#ifndef _CONNECTION_STATE_H_
#define _CONNECTION_STATE_H_

#include "../common/constants.h"

namespace MYLIB 
{
    class ConnectionState 
    {
        private:
          ConStatus _current_state;

        public:
            ConnectionState();
            ConnectionState(ConStatus status);

            ConnectionState(const ConnectionState&) = default;
            ConnectionState& operator=(const ConnectionState&) = default;

            bool isInStatus(ConStatus compare_to) const;
            void updateStatus(ConStatus new_status);
    };
}

#endif