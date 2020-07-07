#include "exception.h"

namespace MYLIB
{
    // --- LibExcp
    LibExcp::LibExcp(const std::string& ms) : std::exception(), _message(ms) { }
    
    const char* LibExcp::what() const throw() { return _message.c_str(); }
    
    int LibExcp::getErrorCode() const { throw std::bad_exception(); };

    // --- FatalExp
    FatalExp::FatalExp(const std::string& mes) : LibExcp(mes) {}

    int FatalExp::getErrorCode() const { return 101; };

    // --- WarningExp
    WarningExp::WarningExp(const std::string& mes) : LibExcp(mes) {}

    int WarningExp::getErrorCode() const { return 201; };

    // --- CriticalExp
    CriticalExp::CriticalExp(const std::string& mes) : LibExcp(mes) {}

    int CriticalExp::getErrorCode() const { return 301; };

    // --- Helper functions
    bool isFatal(int code) { 
        return (code == 101);            
    }

    bool isWarning(int code) { 
        return (code == 201);
    }

    bool isCritical(int code) { 
        return (code == 301);            
    }
}