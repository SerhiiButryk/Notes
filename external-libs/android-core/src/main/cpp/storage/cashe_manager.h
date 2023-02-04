#pragma once

#include <string>
#include <map>
#include <jni.h>

namespace MYLIB
{

    /**
     * Class saves temporary data to a separate file
     *
     * Note: This data is optional and it could be cleared at some time
     */
    class CacheManager
    {
        public:

            JNIEXPORT static void cache(const std::string& file_name, const std::string& key, const std::string& value);
            JNIEXPORT static void clearCache(const std::string& file_name);

            JNIEXPORT static std::string getCachedData(const std::string& key, const std::string& file_name);
            JNIEXPORT static std::map<std::string, std::string> getAllCachedData(const std::string& file_name);
    };

}
