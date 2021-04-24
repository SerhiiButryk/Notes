#pragma once

#include <jni.h>
#include <string>

namespace MYLIB
{
    /**
     * JNI String wrapper class
     *
     * Class provides functionality to convert jni string to std::string
     * and release resources when they are not needed
     *
     * Note: Class is only movable
     */
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

        JString(JString&&) noexcept;
        JString& operator=(const JString&&) noexcept;

        size_t getSize() const;

        operator std::string() const;
        operator const char*() const;
        operator unsigned char*() const;
    };

}
