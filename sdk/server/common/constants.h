#ifndef _CORE_CONSTANTS_H_
#define _CORE_CONSTANTS_H_

namespace MYLIB
{
    // Socket channel
    enum ConStatus
    {
        // Define connection status
        NOT_CONNECTED = -101,
        WAITE_CONNECTION = -102,
        CONNECTED = -103,
    
        // Define communication status after channel is closed
        SESSION_SUCCESSFUL = -104,
        SESSION_FAIL = -105
    };

    extern const int CHANNEL_ONE_ID;
} 

#endif