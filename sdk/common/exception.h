#ifndef _LIB_EXCEPTION_H_
#define _LIB_EXCEPTION_H_

#include <exception>
#include <string>

namespace MYLIB
{
    class LibExcp : public std::exception 
    {
        private:
            std::string _message;

        public:
            LibExcp(const std::string& message);
            
            const char* what() const throw() override;
                        
            virtual int getErrorCode() const;
    };

    /*
    *   Signals about system errors 
    */
    class FatalExp : public LibExcp 
    {
        public:
            FatalExp(const std::string& message);

            int getErrorCode() const override;
    };

    /*
    *   Can be ignored
    */
    class WarningExp : public LibExcp 
    {
        public:
            WarningExp(const std::string& message);

            int getErrorCode() const override;
    };

    /*
    *   Can not be ignored
    */
    class CriticalExp : public LibExcp 
    {
        public:
            CriticalExp(const std::string& message);

            int getErrorCode() const override;
    };

    /*
    *  Helper functions
    */
    bool isFatal(int code); 

    bool isWarning(int code); 

    bool isCritical(int code); 
}

#endif