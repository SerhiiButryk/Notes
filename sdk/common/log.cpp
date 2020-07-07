#include "log.h"
#include <thread>
#include <sstream>
#include <mutex>

namespace MYLIB 
{
    static std::mutex _log_lock;

    #ifndef _LOG_DISABLE_ 

    void logError(const std::string& tag, const std::string& message) 
    {
        std::lock_guard<std::mutex> lock(_log_lock);
        std::cout << _identifier << "    " << __FILE__ << " " 
            << std::setw(_shift_line) 
            << __LINE__ << " " << _E << std::setw(_shift_tag) << tag << "   "
            << std::setw(_shift_message) << message << std::endl;
    }

    void logInfo(const std::string& tag, const std::string& message) 
    {
        std::lock_guard<std::mutex> lock(_log_lock);
        std::cout << _identifier << "    " << __FILE__ << " " 
            << std::setw(_shift_line) 
            << __LINE__ << "    " << _I << std::setw(_shift_tag) << tag << "   "
            << std::setw(_shift_message) << message << std::endl;
    }

    void stdOut(const std::string& tag, const std::string& message) {
        std::lock_guard<std::mutex> lock(_log_lock);
        std::cout << tag << "  " << message << std::endl;
    }

    void logSystem(const std::string& message) {
        std::lock_guard<std::mutex> lock(_log_lock);
        std::cout << "SPL> " << message << std::endl;
    }

    #endif

    // Do not exclude this functions
    void logTest(const std::string& tag, const std::string& message) {
        std::lock_guard<std::mutex> lock(_log_lock);
        std::cout << _identifier << "    " << __FILE__ << " " 
            << std::setw(_shift_line) 
            << __LINE__ << "    " << _I << std::setw(_shift_tag) << tag << "   "
            << std::setw(_shift_message) << message << std::endl;
    }

    void logTestNoInfo(const std::string& tag, const std::string& message) {
        std::lock_guard<std::mutex> lock(_log_lock);
        std::cout << tag << "   " << message << std::endl;
    }

    void logTestNoInfo(const std::string& message) {
        std::lock_guard<std::mutex> lock(_log_lock);
        std::cout << message << std::endl;
    }

    std::string getThreadID() 
    {
        std::stringstream s;
        s << std::this_thread::get_id();
        return s.str();
    } 
}