#pragma once

#include <mutex>

namespace MYLIB
{
    class Lock
    {
    private:
        std::mutex m;
        std::unique_lock<std::mutex> l;

    public:
        Lock();
        ~Lock();

        Lock(const Lock&) = delete;
        Lock& operator=(const Lock&) = delete;

        Lock(Lock&&) = delete;
        Lock& operator=(Lock&&) = delete;
    };

}