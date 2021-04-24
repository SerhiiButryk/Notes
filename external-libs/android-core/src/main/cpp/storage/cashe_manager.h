#pragma once

#include <string>
#include <map>

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

            static void cache(const std::string& file_name, const std::string& key, const std::string& value);
            static void clearCache(const std::string& file_name);

            static std::string getCachedData(const std::string& key, const std::string& file_name);
            static std::map<std::string, std::string> getAllCachedData(const std::string& file_name);
    };

}
