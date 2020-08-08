#pragma once

#include <jni.h>
#include <string>

namespace MYLIB
{

    class JString
    {
    private:
        const char* _charString = nullptr;
        jstring _jstring;
        JNIEnv* _env = nullptr;

    public:
        JString(JNIEnv* env, jstring string);
        ~JString();

        JString(const JString&) = delete;
        JString& operator=(const JString&) = delete;

        operator std::string() const;
        operator const char*() const;
    };

}
