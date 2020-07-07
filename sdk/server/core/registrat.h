#ifndef _NetConnection_H_
#define _NetConnection_H_

#include <map>
#include <set>
#include <vector>

#include "../net/connection_state.h"
#include "../net/connection_receiver.h"

/**
 *  Register server channel listeners
*/

namespace MYLIB 
{
    class NetConnection
    {
        friend class ConnectionWatcher;

        private:
            std::map<int, ConnectionState> _channels;
            std::set<int> _channel_ids;

            // Constructor
            NetConnection();

            void updateStatus(int channel_id, ConStatus new_status);
            bool isRegistred(int channel_id);

        public:
            static NetConnection* getInstance();
            
            void registerChannel(int channel_id);
            const ConnectionState& getConnectionStatus(int channel_id);

            void showDebug() const;
    };
}

#endif