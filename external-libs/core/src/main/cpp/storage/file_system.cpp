#include "file_system.h"

#include <sstream>
#include <exception>
#include <utility>

#include "utils/log.h"

const static std::string TAG = "FileSystem";

namespace MYLIB
{

    const std::string FileSystem::FileSeparator = "/";

    FileSystem* FileSystem::getInstance()
    {
        static FileSystem f;
        return &f;
    }

    void FileSystem::initFilePath(const std::string& path)
    {
        env_path = path;

        // DEBUG ONLY
        // Log::Info("FileSystem", "initFilePath(): Path: %s \n", path);
    }

    std::string FileSystem::getFilePath() const
    {
        return env_path + FileSeparator;
    }

    bool FileSystem::isEmpty(const std::string& file_name) const
    {
        if (isExists(file_name))
        {
            std::fstream fs;
            fs.open(getAbsoluteFilePath(file_name));

            bool is_empty = false;

            if (fs.is_open())
            {
                is_empty = fs.peek() == std::fstream::traits_type::eof();
                fs.close();
            }

            return is_empty;
        }

        return false;
    }

    std::string FileSystem::getAbsoluteFilePath(const std::string &file_name) const
    {
        if (env_path.empty()) {
            throw std::runtime_error("[File system error] File system is not initialized");
        }

        return std::string(env_path + FileSeparator + file_name);
    }

    bool FileSystem::openFile(const std::string &file_name, std::fstream& file)
    {
        std::string path = getFilePath() + file_name;

        // std::fstream for some reason can't create a file if it doesn't exist
        // first use C++ API then switch back to C API if it fails

        if (!_open_f(path, file))
        {
            bool success = _copen_f(path);

            if (success)
            {
                // Retry
                return _open_f(path, file);

            } else {
                Log::Info(TAG, "openFile(%s): File was not opened \n", file_name);
            }
        }

        return true;
    }

    bool FileSystem::openFile(const std::string &file_name, std::fstream& file, int mode)
    {
        std::string path = getFilePath() + file_name;

        // std::fstream for some reason can't create a file if it doesn't exist
        // first use C++ API then switch back to C API if it fails

        if (!_open_f(path, file, mode))
        {
            bool success = _copen_f(path);

            if (success)
            {
                // Retry
                return _open_f(path, file, mode);

            } else {
                Log::Info(TAG, "openFile(%s): File is not opened \n", file_name);
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
