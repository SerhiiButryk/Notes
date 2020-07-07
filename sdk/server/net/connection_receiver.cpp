#include "connection_receiver.h"

#include <algorithm>

#include "../core/registrat.h"
#include "../../common/exception.h"

namespace MYLIB
{
    void ConnectionWatcher::update(int channel_id, int success_code) {
        NetConnection* registrat = NetConnection::getInstance();

        if (registrat == nullptr) {
            throw CriticalExp("null pointer in ConnectionWatcher::update()");
        }
        
        registrat->updateStatus(channel_id, success_code);

        notifyAll(success_code, channel_id);
    }
    
    void ConnectionWatcher::subscribe(ICStatus* state_update_receiver) {
        _observers.push_back(state_update_receiver);
    }

    void ConnectionWatcher::unsubscribe(ICStatus* state_update_receiver) {
        _observers.erase(std::remove(_observers.begin(), _observers.end(), state_update_receiver));
    }
    
    void ConnectionWatcher::unsubscribeAll() {
        for (const auto& obs : _observers) {
            _observers.erase(std::remove(_observers.begin(), _observers.end(), obs));
        }
    }

    void ConnectionWatcher::notifyAll(int new_state, int channel_id) {
        for (const auto& obs : _observers) {
            if (obs->getChannel() == channel_id)
                obs->notify(new_state);
        }
    }

    ConnectionWatcher* ConnectionWatcher::getInstance() {
        static ConnectionWatcher watcher;
        return &watcher;
    }

}