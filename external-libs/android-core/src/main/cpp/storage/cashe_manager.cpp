#include "cashe_manager.h"

#include "storage/system_storage.h"

namespace MYLIB
{

    JNIEXPORT std::string CacheManager::getCachedData(const std::string& key, const std::string& file_name)
    {
        SystemStorage ss;

        std::map<std::string, std::string> data = ss.readData(file_name);

        auto it = data.find(key);

        if (it != data.end())
        {
            return it->second;
        }

        return "";
    }

    JNIEXPORT void CacheManager::cache(const std::string& file_name, const std::string& key, const std::string& value)
    {
        SystemStorage ss;

        // File exists
        if (ss.doesFileExist(file_name))
        {
            std::map<std::string, std::string> _old_data = ss.readData(file_name);

            auto it = _old_data.find(key);

            if (it == _old_data.end())
            {
                _old_data.insert(std::make_pair(key, value));

                ss.overrideData(file_name, _old_data);

            } else {

                it->second = value;

                ss.overrideData(file_name, _old_data);
            }

        } else {

            // File doesn't exist
            if (ss.createFile(file_name))
            {
                std::map<std::string, std::string> data;
                data.insert(std::make_pair(key, value));

                ss.addData(file_name, data);
            }

        }

    }

    JNIEXPORT void CacheManager::clearCache(const std::string& file_name)
    {
        SystemStorage ss;

        if (ss.doesFileExist(file_name)) {
            ss.clearData(file_name);
        }

    }

    JNIEXPORT std::map<std::string, std::string> CacheManager::getAllCachedData(const std::string &file_name)
    {
        SystemStorage ss;

        return ss.readData(file_name);
    }

}
