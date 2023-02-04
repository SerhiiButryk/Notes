#pragma once

#include <string>
#include <map>
#include <jni.h>

namespace MYLIB
{

    /**
     *  Class provides functionality to save and retrieve data from a file.
     */
    class SystemStorage
    {
        public:

            JNIEXPORT void overrideData(const std::string& file_name, const std::map<std::string, std::string>& data) const;

            JNIEXPORT void addData(const std::string& file_name, const std::map<std::string, std::string>& data) const;

            JNIEXPORT std::map<std::string, std::string> readData(const std::string& file_name) const;

            JNIEXPORT bool updateData(const std::string& file_name, const std::string& key, const std::string& new_value) const;

            JNIEXPORT bool doesFileExist(const std::string& file_name) const;
            JNIEXPORT void clearData(const std::string& file_name) const;
            JNIEXPORT void removeData(const std::string& file_name) const;

            JNIEXPORT bool doesValueByKeyExist(const std::string& file_name, const std::string& key, const std::string& value) const;
            JNIEXPORT bool doesKeyExist(const std::string& file_name, const std::string& key) const;

            JNIEXPORT bool createFile(const std::string& file_name) const;

            JNIEXPORT std::string getDataByKey(const std::string& file_name, const std::string& key) const;

        private:

            bool writeData(const std::string& file_name, const std::map<std::string, std::string>& data,
                           bool shouldAppend = false) const;

        public:
            void debugPrintAllValues(const std::string& file_name) const;
    };

}
