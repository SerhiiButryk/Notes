#include <algorithm>
#include <iostream>
#include <sstream>

#include "registrat.h"
#include "../../common/exception.h"
#include "../../common/log.h"

namespace MYLIB 
{
    static const std::string TAG = "Registrat";

    NetConnection::NetConnection() {}

    NetConnection* NetConnection::getInstance() {
        static NetConnection registrat;
        return &registrat;
    }
    
    void NetConnection::registerChannel(int channel_id) {
         // register id channel
        _channel_ids.insert(channel_id);
    
        // register result execution by channel id
        _channels.insert(std::pair<int, ConnectionState>(channel_id, ConnectionState()));
    }

    void NetConnection::updateStatus(int channel_id, ConStatus new_status) {

        logInfo(TAG, std::string("channel id ") + std::to_string(channel_id));

        if (!isRegistred(channel_id))
            throw CriticalExp("In NetConnection::updateStatus trying to update not registred channel");

        _channels[channel_id] = new_status;    
    }

    bool NetConnection::isRegistred(int channel_id) {
        return _channel_ids.find(channel_id) != _channel_ids.end();
    }

    const ConnectionState& NetConnection::getConnectionStatus(int channel_id) {
        if (_channels.find(channel_id) == _channels.end()) {
            throw CriticalExp("Channel id was not registred");
        }
        
        return _channels[channel_id];
    }

    void NetConnection::showDebug() const {
        std::stringstream ss;
        
        ss << "\n----DEBUG REGISTRAT----\n";
        ss << _channel_ids.size() << "\n";
        
        for (const auto& i : _channel_ids) 
            ss << i << "\n";

        ss << "----DEBUG NetConnection----\n";

        logInfo("NetConnection", ss.str().c_str());
    }

}