#include "file_system.h"

#include <sstream>
#include <exception>
#include <utility>

#include "utils/log.h"

namespace {
    const std::string TAG = "FileSystem";

    // TODO: Set from Java
    const std::string FileSeparator = "/";
}

namespace MYLIB
{
    FileSystem* FileSystem::getInstance()
    {
        static FileSystem f;
        return &f;
    }

    void FileSystem::initFilePath(const std::string& path)
    {
        env_path = path;
        Log::Info("FileSystem", "initFilePath(): INITIALIZED \n");
    }

    std::string FileSystem::getFilePath() const
    {
        Log::Info(TAG, "getFilePath() \n");

        if (env_path.empty())
        {
            Log::Error(TAG, "getFilePath(): [File system error] File system is not initialized, THROW EXCEPTION \n");
            throw std::runtime_error("[File system error] File system is not initialized");
        }

        return env_path + FileSeparator;
    }

    bool FileSystem::isEmpty(const std::string& file_name) const
    {
        if (isExists(file_name))
        {
            std::fstream fs;
            fs.open(getAbsoluteFilePath(file_name));

            if (fs.is_open())
            {
                bool is_empty = fs.peek() == std::fstream::traits_type::eof();
                fs.close();

                return is_empty;
            }

        }

        return false;
    }

    std::string FileSystem::getAbsoluteFilePath(const std::string &file_name) const
    {
        Log::Info(TAG, "getAbsoluteFilePath(): file name: %s \n", file_name);

        return std::string(getFilePath() + file_name);
    }

    bool FileSystem::openFile(const std::string &file_name, std::fstream& file)
    {
        std::string path = getFilePath() + file_name;

        // ANDROID ISSUE
        //
        // std::fstream for some reason can't create a file in android file system.
        // We use C++ FStream API and if the creation is failed, then we switch back to C File API.

        if (!_open_f(path, file))
        {
            bool success = _copen_f(path);

            if (success)
            {
                // Retry
                return _open_f(path, file);

            } else {
                Log::Error(TAG, "openFile(): File was not opened, fle name: %s \n", file_name);
            }
        }

        return true;
    }

    bool FileSystem::openFile(const std::string &file_name, std::fstream& file, int mode)
    {
        std::string path = getFilePath() + file_name;

        // ANDROID ISSUE
        //
        // std::fstream for some reason can't create a file in android file system.
        // We use C++ FStream API and if the creation is failed, then we switch back to C File API.

        if (!_open_f(path, file, mode))
        {
            bool success = _copen_f(path);

            if (success)
            {
                // Retry
                return _open_f(path, file, mode);

            } else {
                Log::Error(TAG, "openFile(mode): File was not opened, fle name: %s \n", file_name);
            }
        }

        return true;
    }

    bool FileSystem::_copen_f(const std::string& path)
    {
        FILE* _file = nullptr;
        _file = fopen(path.c_str(), "wr");

        if (_file != nullptr)
        {
            fclose(_file);

            return true;
        }

        return false;
    }

    bool FileSystem::_open_f(const std::string& path, std::fstream& file, std::ios_base::openmode mode)
    {
        file.open(path, mode);
        return  file.is_open();
    }

    bool FileSystem::_open_f(const std::string& path, std::fstream& file)
    {
        file.open(path);
        return  file.is_open();
    }

    void FileSystem::writeFile(const std::map<std::string, std::string>& data, std::fstream& file)
    {
        if (!file.is_open()) return;

        std::ostringstream oss;

        for (auto iter = data.begin(); iter != data.end(); iter++)
        {
            oss << iter->first << " " << iter->second << "\n";
        }

        file << oss.str();

        file.close();
    }

    std::map<std::string, std::string> FileSystem::readFile(std::fstream& file)
    {
        if (!file.is_open()) return std::map<std::string, std::string>();

        std::map<std::string, std::string> _data;

        std::string key, value, line;

        while (std::getline(file, line))
        {
            std::istringstream iss(line);

            iss >> key >> value;

            _data.insert(std::make_pair(key, value));
        }

        file.close();

        return _data;
    }

    bool FileSystem::isExists(const std::string &file_name) const
    {
        std::fstream fs;

        fs.open(getAbsoluteFilePath(file_name));

        bool rez = fs.is_open();

        if (rez)
        {
            fs.close();
        }

        return rez; // If file does not exist this will return false
    }

    bool FileSystem::append(const std::map<std::string, std::string>& data, const std::string& file_name)
    {
        std::fstream file;

        if (openFile(file_name, file, std::ios_base::app))
        {
            std::ostringstream oss;

            for (const auto& v : data)
            {
                oss << v.first << " " << v.second << std::endl;
            }

            file << oss.str();

            file.close();

            return true;
        }

        return false;
    }
}
