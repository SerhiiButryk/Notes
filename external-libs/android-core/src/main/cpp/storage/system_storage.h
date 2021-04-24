#pragma once

#include <string>
#include <map>

namespace MYLIB
{

    /**
     *  Class provides functionality to save and retrieve data from a file
     */
    class SystemStorage
    {
        public:

            void overrideData(const std::string& file_name, const std::map<std::string, std::string>& data) const;

            void addData(const std::string& file_name, const std::map<std::string, std::string>& data) const;

            std::map<std::string, std::string> readData(const std::string& file_name) const;

            bool updateData(const std::string& file_name, const std::string& key, const std::string& new_value) const;

            bool doesFileExist(const std::string& file_name) const;
            void clearData(const std::string& file_name) const;
            void removeData(const std::string& file_name) const;

            bool doesValueByKeyExist(const std::string& file_name, const std::string& key, const std::string& value) const;
            bool doesKeyExist(const std::string& file_name, const std::string& key) const;

            bool createFile(const std::string& file_name) const;

            std::string getDataByKey(const std::string& file_name, const std::string& key) const;

        private:

            bool writeData(const std::string& file_name, const std::map<std::string, std::string>& data,
                           bool shouldAppend = false) const;

        public:
            void debugPrintAllValues(const std::string& file_name) const;
    };

}
