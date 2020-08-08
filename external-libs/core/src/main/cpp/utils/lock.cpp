#include "lock.h"

using namespace MYLIB;

Lock::Lock() : l(m, std::defer_lock)
{
    l.lock();
}

Lock::~Lock()
{
    l.unlock();
}
