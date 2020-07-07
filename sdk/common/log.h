#ifndef _LOG_H
#define _LOG_H

#include <iostream>
#include <iomanip>
#include <string>

#define _LOG_DISABLE_ // Disable all logs

namespace MYLIB 
{
    const int _shift_tag = 16;
    const int _shift_line = 5;
    const int _shift_message = 10;
    const std::string _identifier = "LOG";
    const std::string _I = "I/";
    const std::string _E = "E/";

    #ifndef _LOG_DISABLE_

        #define LOG_INFO(x, y) std::cout << _identifier << " " << __FILE__ << " " \
            << std::setw(_shift_line) << __LINE__ << " " << _I << std::setw(_shift_tag) \
            << x << std::setw(_shift_message) << y << std::endl;

        #define LOG_ERROR(x, y) std::cout << _identifier << " " << __FILE__ << " " \
            << std::setw(_shift_line) << __LINE__ << " " << _E << std::setw(_shift_tag) \
            << x << std::setw(_shift_message) << y << std::endl;   

            void logError(const std::string& tag, const std::string& message);
            void logInfo(const std::string& tag, const std::string& message);
            void stdOut(const std::string& tag, const std::string& message);
            void logSystem(const std::string& message);

    #else 

        #define LOG_INFO(x, y) 
        #define LOG_ERROR(x, y) 

        #define logError(x,y);
        #define logInfo(x,y);
        #define stdOut(x,y);
        #define logSystem(x);

    #endif

    // Do not exclude this functions
    void logTest(const std::string& tag, const std::string& message);
    void logTestNoInfo(const std::string& tag, const std::string& message);
    void logTestNoInfo(const std::string& message);
    
    std::string getThreadID();
}

#endif