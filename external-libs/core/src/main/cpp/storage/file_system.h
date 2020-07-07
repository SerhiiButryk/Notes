#ifndef ANDROID_SAMPLES_FILE_SYSTEM_H
#define ANDROID_SAMPLES_FILE_SYSTEM_H

#include <string>
#include <map>
#include <fstream>
#include "system_storage.h"

/**
 *  Class handles low level file operations.
 *  Read/Write in file as key-value pair data.
 */

namespace MYLIB
{

    class FileSystem
    {
        friend class SystemStorage;

        public:
            static const std::string FileSeparator;

        private:
            std::string env_path;

            bool _copen_f(const std::string& path);
            bool _open_f(const std::string& path, std::fstream& file);
            bool _open_f(const std::string& path, std::fstream& file, std::ios_base::openmode mode);

        public:
            bool openFile(const std::string& file_name, std::fstream& file);
            bool openFile(const std::string& file_name, std::fstream& file, int mode);

            void writeFile(const std::map<std::string, std::string>& data, std::fstream& file);
            bool append(const std::map<std::string, std::string>& data, const std::string &file_name);

            std::map<std::string, std::string> readFile(std::fstream& file);

            bool isExists(const std::string& file_name) const;
            bool isEmpty(const std::string& file_name) const;

            std::string getAbsoluteFilePath(const std::string &file_name) const;
            std::string getFilePath() const;

            static FileSystem* getInstance();

            void initFilePath(const std::string& path);
    };

}

#endif