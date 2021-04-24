#include "system_storage.h"

#include "file_system.h"
#include "utils/log.h"

namespace {
    // For DEBUG
    const std::string TAG = "SystemStorage";
}

namespace MYLIB
{

    bool SystemStorage::writeData(const std::string& file_name, const std::map<std::string, std::string>& data, bool shouldAppend) const
    {
        FileSystem* fs = FileSystem::getInstance();

        std::fstream file;

        if (shouldAppend)
        {
            return fs->append(data, file_name);
        }

        if (fs->openFile(file_name, file))
        {
            fs->writeFile(data, file);

            return true;
        }

        return false;
    }

    void SystemStorage::overrideData(const std::string& file_name, const std::map<std::string, std::string>& data) const
    {
        FileSystem* fileSystem = FileSystem::getInstance();

        std::fstream file;

        int mode = std::ios_base::out | std::ios_base::trunc;

        bool is_opened = fileSystem->openFile(file_name, file, mode);

        if (is_opened)
        {
            fileSystem->writeFile(data, file);
        }

    }

    void SystemStorage::addData(const std::string& file_name, const std::map<std::string, std::string>& data) const
    {
        if (!writeData(file_name, data, true))
        {
            Log::Error(TAG, "addData(): Failed to add data, file is not opened, file name: %s \n", file_name);
        }
    }

    std::map<std::string, std::string> SystemStorage::readData(const std::string& file_name) const
    {
        FileSystem* fs = FileSystem::getInstance();

        std::map<std::string, std::string> data;
        std::fstream file;

        if (fs->openFile(file_name, file))
        {
            data = fs->readFile(file);
        } else {
            Log::Error(TAG, "readData(): Failed to read data, file was not opened, file name: %s \n", file_name);
        }

        // UNCOMMENT FOR DEBUGGING
        // debugPrintAllValues(file_name);

        return data;
    }

    bool SystemStorage::updateData(const std::string& file_name, const std::string& key, const std::string& new_value) const
    {
        std::map<std::string, std::string> data = readData(file_name);
        std::map<std::string, std::string>::iterator it = data.begin();

        bool isFound = false;

        while (it != data.end())
        {
            if (it->first == key)
            {
                it->second = new_value;
                isFound = true;
            }

            ++it;
        }

        if (isFound)
        {
            overrideData(file_name, data);
        }

        return isFound;
    }

    bool SystemStorage::doesFileExist(const std::string& file_name) const
    {
        return FileSystem::getInstance()->isExists(file_name);
    }

    void SystemStorage::debugPrintAllValues(const std::string& file_name) const
    {
        if (!doesFileExist(file_name))
        {
            Log::Error(TAG, "debugPrintAllValues(): File does not exist, file name: %s \n", file_name);
            return;
        }

        auto values = readData(file_name);

        for (auto& v : values)
        {
            Log::Info(TAG, "debugPrintAllValues(): File data : %s : %s :: file name : %s \n", v.first, v.second, file_name);
        }

    }

    void SystemStorage::clearData(const std::string &file_name) const
    {
        std::ofstream ofs;

        FileSystem* fs = FileSystem::getInstance();

        bool success = fs->_copen_f(fs->getAbsoluteFilePath(file_name));

        if (success) {

            ofs.open(file_name, std::ofstream::out | std::ofstream::trunc);

            if (ofs.is_open())
            {
                Log::Info(TAG, "clearData(): File is cleared, file name: %s \n", file_name);

                ofs.close();
            }

        }

    }

    void SystemStorage::removeData(const std::string& file_name) const
    {
        FileSystem* fs = FileSystem::getInstance();

        // Remove by absolute file path
        std::remove(fs->getAbsoluteFilePath(file_name).c_str());
    }

    bool SystemStorage::doesValueByKeyExist(const std::string& file_name, const std::string& key, const std::string& value) const
    {
        if (!doesFileExist(file_name))
            return false;

        auto values = readData(file_name);

        for (auto& v : values) {

            if (v.first == key && v.second == value)
                return true;

        }

        return false;
    }

    bool SystemStorage::createFile(const std::string& file_name) const
    {
        if (doesFileExist(file_name))
        {
            Log::Info(TAG, "createFile(): File does already exist, file name: %s \n", file_name);
            return false;
        }

        std::map<std::string, std::string> empty_map;

        // Create new empty file
        return writeData(file_name, empty_map, false);
    }

    bool SystemStorage::doesKeyExist(const std::string& file_name, const std::string& key) const
    {
        if (!doesFileExist(file_name))
            return false;

        auto values = readData(file_name);

        for (auto& v : values) {

            if (v.first == key)
                return true;

        }

        return false;
    }

    std::string SystemStorage::getDataByKey(const std::string& file_name, const std::string& key) const
    {
        if (!doesFileExist(file_name))
            return std::string();

        auto values = readData(file_name);

        for (auto& v : values) {

            if (v.first == key) {
                return v.second;
            }

        }

        return std::string();
    }

}
