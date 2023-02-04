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
        JNIEXPORT JString(JNIEnv* env, jstring string);
        JNIEXPORT ~JString();

        JNIEXPORT JString(const JString&) = delete;
        JNIEXPORT JString& operator=(const JString&) = delete;

        JNIEXPORT JString(JString&&) noexcept;
        JNIEXPORT JString& operator=(const JString&&) noexcept;

        JNIEXPORT size_t getSize() const;

        JNIEXPORT operator std::string() const;
        JNIEXPORT operator const char*() const;
        JNIEXPORT operator unsigned char*() const;
    };

}
