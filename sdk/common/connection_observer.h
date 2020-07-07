#ifndef _OBSERVER_H_
#define _OBSERVER_H_

namespace MYLIB
{
    class ICStatus // Connection status interface
    {
        private:
            int _channel_id;

        public:
            ICStatus() = default;
            virtual ~ICStatus() = default;

            virtual void notify(int new_status) = 0;
            
            virtual void setChannel(int channel_id) { _channel_id = channel_id; }

            virtual int getChannel() const { return _channel_id; }
    };
    
}

#endif