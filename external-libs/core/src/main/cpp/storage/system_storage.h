#ifndef ANDROID_SAMPLES_SYSTEM_STORAGE_H
#define ANDROID_SAMPLES_SYSTEM_STORAGE_H

#include <string>
#include <map>

namespace MYLIB
{

    class SystemStorage
    {
        public:

            void overrideFile(const std::string& file_name, const std::map<std::string, std::string>& data);

            void addValues(const std::string& file_name, const std::map<std::string, std::string>& data);

            std::map<std::string, std::string> getValues(const std::string& file_name) const;

            bool updateValue(const std::string& file_name, const std::string& key, const std::string& new_value);

            bool doesFileExist(const std::string& file_name) const;
            void clearFileData(const std::string& file_name) const;
            void removeFileData(const std::string& file_name) const;

            bool doesValueByKeyExist(const std::string& file_name, const std::string& key, const std::string& value) const;
            bool doesKeyExist(const std::string& file_name, const std::string& key) const;

            bool createFile(const std::string& file_name);

            std::string getValueByKey(const std::string& file_name, const std::string& key) const;

        private:

            bool writeData(const std::string& file_name, const std::map<std::string, std::string>& data,
                           bool shouldAppend = false);

        public:
            void debugPrintAllValues(const std::string& file_name) const;
    };

}


#endif
