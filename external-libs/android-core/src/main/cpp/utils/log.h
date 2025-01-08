#pragma once

#include <android/log.h>
#include <string>
#include <sstream>
#include <mutex>
#include <jni.h>

namespace MYLIB
{

    #define Error(TAG, ...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__);
    #define Info(TAG, ...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__);

    /**
     * Class provides common logging functionality.
     * It uses Boost for message formatting.
     */
    class Log
    {
        public:

            /**
             * Detailed logs
             */
            JNIEXPORT static bool isDetailedLogsEnabled();
            JNIEXPORT static void setDetailedLogsEnabled(bool isDetailedLogsEnabled);

        /**
         * Sets tag for log filtering
         * @param tag - tag for filtering
         */
            JNIEXPORT static void setTag(const std::string& tag);

        private:

            JNIEXPORT static std::string _TAG_APP_;

            static bool detailedLogsEnabled;

    };

}
