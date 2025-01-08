#include "log.h"

#include <iomanip>

namespace MYLIB
{
    JNIEXPORT std::string Log::_TAG_APP_ = "";

    bool Log::detailedLogsEnabled = false;

    JNIEXPORT void Log::setTag(const std::string& tag) {
        _TAG_APP_ = tag;
    }

    JNIEXPORT bool Log::isDetailedLogsEnabled()
    {
        return detailedLogsEnabled;
    }

    JNIEXPORT void Log::setDetailedLogsEnabled(bool logsEnabled)
    {
        detailedLogsEnabled = logsEnabled;
    }

}
