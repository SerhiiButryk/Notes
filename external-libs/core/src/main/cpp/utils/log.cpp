#include "log.h"

#include <iomanip>

namespace MYLIB
{
    void Log::log(int LOG_LEVEL, const std::string& TAG, const std::string& formattedMessage) {

        std::lock_guard guard(_mutex_log_guard);

        __android_log_write(LOG_LEVEL, TAG.c_str(), formattedMessage.c_str());
    }

    void Log::Error(const std::string& TAG, const std::string& message) {

        log(ANDROID_LOG_ERROR, _TAG_APP_, TAG + " " + message);
    }

    void Log::Info(const std::string& TAG, const std::string& message) {

        log(ANDROID_LOG_INFO, _TAG_APP_, TAG + " " + message);
    }

    std::string Log::_TAG_APP_ = "";

    void Log::setTag(const std::string& tag) {

        _TAG_APP_ = tag;
    }

    std::mutex Log::_mutex_log_guard;

}
