#pragma once

#include <string>
#include <map>
#include <fstream>
#include <jni.h>
#include "system_storage.h"

namespace MYLIB
{

    /**
     *  Class handles low level read/write operations to file system.
     *
     *  Note: Read/Write from/to file as key-value pair data.
     *
     *  Also note that system root directory path should be set before accessing the file storage.
     */
    class FileSystem
    {
        friend class SystemStorage;

        private:
            std::string env_path;

            bool _copen_f(const std::string& path);
            bool _open_f(const std::string& path, std::fstream& file);
            bool _open_f(const std::string& path, std::fstream& file, std::ios_base::openmode mode);

        public:
            JNIEXPORT bool openFile(const std::string& file_name, std::fstream& file);
            JNIEXPORT bool openFile(const std::string& file_name, std::fstream& file, int mode);

            JNIEXPORT void writeFile(const std::map<std::string, std::string>& data, std::fstream& file);
            JNIEXPORT bool append(const std::map<std::string, std::string>& data, const std::string &file_name);

            JNIEXPORT std::map<std::string, std::string> readFile(std::fstream& file);

            JNIEXPORT bool isExists(const std::string& file_name) const;
            JNIEXPORT bool isEmpty(const std::string& file_name) const;

            JNIEXPORT std::string getAbsoluteFilePath(const std::string &file_name) const;
            JNIEXPORT std::string getFilePath() const;

            JNIEXPORT static FileSystem* getInstance();

            JNIEXPORT void initFilePath(const std::string& path);
    };

}
