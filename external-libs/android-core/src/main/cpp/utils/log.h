#pragma once

#include <android/log.h>
#include <string>
#include <sstream>
#include <mutex>
#include <jni.h>

#define BOOST_NO_AUTO_PTR
#include "boost/format.hpp"

namespace MYLIB
{

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
            JNIEXPORT static bool detailedLogsEnabled;
            JNIEXPORT static bool isDetailedLogsEnabled();
            JNIEXPORT static void setIsDetailedLogsEnabled(bool isDetailedLogsEnabled);

        /**
         * Sets tag for log filtering
         * @param tag - tag for filtering
         */
            JNIEXPORT static void setTag(const std::string& tag);

            template<typename T, typename... Args>
            JNIEXPORT static void Error(const std::string& TAG, const std::string& message, const T& arg, const Args&... args) {

                log(ANDROID_LOG_ERROR, _TAG_APP_, TAG + " " + message, arg, args...);
            }

            template<typename T>
            JNIEXPORT static void Error(const std::string& TAG, const std::string& message, const T& arg) {

                log(ANDROID_LOG_ERROR, _TAG_APP_, TAG + " " + message, arg);
            }

            JNIEXPORT static void Error(const std::string& TAG, const std::string& message);

            template<typename T, typename... Args>
            JNIEXPORT static void Info(const std::string& TAG, const std::string& message, const T& arg, const Args&... args) {

                log(ANDROID_LOG_INFO, _TAG_APP_, TAG + " " + message, arg, args...);
            }

            template<typename T>
            JNIEXPORT static void Info(const std::string& TAG, const std::string& message, const T& arg) {

                log(ANDROID_LOG_INFO, _TAG_APP_, TAG + " " + message, arg);
            }

            JNIEXPORT static void Info(const std::string& TAG, const std::string& message);

        private:

            JNIEXPORT static std::string _TAG_APP_;

            JNIEXPORT static std::mutex _mutex_log_guard; // Protects android log resource

            template<typename T, typename... Args>
            JNIEXPORT static void log(int LOG_LEVEL, const std::string& TAG, const std::string& formattedMessage, const T& arg, const Args&... args) {

                std::lock_guard guard(_mutex_log_guard);

                using boost::format;

                format formatter(formattedMessage);
                formatter % arg;
                formatter % getArg(formatter, args...);

                __android_log_write(LOG_LEVEL, TAG.c_str(), formatter.str().c_str());
            }

            template<typename T>
            JNIEXPORT static void log(int LOG_LEVEL, const std::string& TAG, const std::string& formattedMessage, const T& arg) {

                std::lock_guard guard(_mutex_log_guard);

                using boost::format;

                format formatter(formattedMessage);
                formatter % arg;

                __android_log_write(LOG_LEVEL, TAG.c_str(), formatter.str().c_str());
            }

            JNIEXPORT static void log(int LOG_LEVEL, const std::string& TAG, const std::string& formattedMessage);

            template<typename T, typename... Args>
            JNIEXPORT static const T& getArg(boost::format& f, const T& arg, const Args&... args) {
                f % arg;
                return getArg(f, args...);
            }

            template<typename T>
            JNIEXPORT static const T& getArg(boost::format& f, const T& arg) {
                return arg;
            }

    };

}
