#pragma once

#include <string>

namespace MYLIB
{

    class CacheManager
    {
        public:

            void cache(const std::string& file_name, const std::string& key, const std::string& value);
            void clearCash(const std::string& file_name);

            std::string getCachedValue(const std::string& key, const std::string& file_name) const;
    };

}
