#include "cashe_manager.h"

#include <map>

#include "storage/system_storage.h"

namespace MYLIB
{

    std::string CacheManager::getCachedValue(const std::string& key, const std::string& file_name) const
    {
        SystemStorage ss;

        std::map<std::string, std::string> data = ss.getValues(file_name);

        auto it = data.find(key);

        if (it != data.end())
        {
            return it->second;
        }

        return "";
    }

    void CacheManager::cache(const std::string& file_name, const std::string& key, const std::string& value)
    {
        SystemStorage ss;

        if (ss.doesFileExist(file_name))
        {
            std::map<std::string, std::string> _old_data = ss.getValues(file_name);

            auto it = _old_data.find(key);

            if (it == _old_data.end())
            {
                _old_data.insert(std::make_pair(key, value));
            }

            ss.overrideFile(file_name, _old_data);

        } else {

            if (ss.createFile(file_name))
            {
                std::map<std::string, std::string> data;
                data.insert(std::make_pair(key, value));

                ss.addValues(file_name, data);
            }

        }

    }

    void CacheManager::clearCash(const std::string& file_name)
    {
        SystemStorage ss;

        if (ss.doesFileExist(file_name)) {
            ss.clearFileData(file_name);
        }

    }
}
