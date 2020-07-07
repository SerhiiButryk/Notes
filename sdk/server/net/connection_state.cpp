#include "connection_state.h"

namespace MYLIB 
{
    ConnectionState::ConnectionState() : _current_state(NOT_CONNECTED) {
    }

    ConnectionState::ConnectionState(ConStatus status_code) : _current_state(status_code) {
    }

    void ConnectionState::updateStatus(ConStatus new_status) {
        _current_state = new_status;        
    }

    bool ConnectionState::isInStatus(ConStatus compare_to) const {
        return _current_state == compare_to;
    }
    
}