#include "log.h"

#include <iomanip>

namespace MYLIB
{
    JNIEXPORT std::mutex Log::_mutex_log_guard;
    JNIEXPORT std::string Log::_TAG_APP_ = "";
    bool Log::detailedLogsEnabled = false;

    void Log::log(int LOG_LEVEL, const std::string& TAG, const std::string& formattedMessage) {

        std::lock_guard guard(_mutex_log_guard);

        __android_log_write(LOG_LEVEL, TAG.c_str(), formattedMessage.c_str());
    }

    JNIEXPORT void Log::Error(const std::string& TAG, const std::string& message) {

        log(ANDROID_LOG_ERROR, _TAG_APP_, TAG + " " + message);
    }

    JNIEXPORT void Log::Info(const std::string& TAG, const std::string& message) {

        log(ANDROID_LOG_INFO, _TAG_APP_, TAG + " " + message);
    }

    JNIEXPORT void Log::setTag(const std::string& tag) {

        std::lock_guard guard(_mutex_log_guard);

        _TAG_APP_ = tag;
    }

    JNIEXPORT bool Log::isDetailedLogsEnabled() {
        return detailedLogsEnabled;
    }

    JNIEXPORT void Log::setIsDetailedLogsEnabled(bool logsEnabled) {
        Log::detailedLogsEnabled = logsEnabled;
    }

}
