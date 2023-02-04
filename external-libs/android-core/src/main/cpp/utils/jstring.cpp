#include "jstring.h"

#include <cstring>

namespace MYLIB
{

    JNIEXPORT JString::JString(JNIEnv* env, jstring string)
    {
        if (string != nullptr)
        {
            _charString = env->GetStringUTFChars(string, 0);
            _env = env;
            _jstring = string;
        }
    }

    JNIEXPORT JString::~JString()
    {
        if (_charString != nullptr)
        {
            _env->ReleaseStringUTFChars(_jstring, _charString);
        }
    }

    JNIEXPORT JString::JString(JString&& from) noexcept : _charString(from._charString),
        _jstring(from._jstring), _env(from._env)
    {
        from._charString = nullptr;
        from._jstring = nullptr;
        from._env = nullptr;
    }

    JNIEXPORT JString& JString::operator=(const JString&& from) noexcept
    {
        if (&from != this)
        {
            _charString = from._charString;
            _jstring = from._jstring;
            _env = from._env;
        }

        return *this;
    }

    JNIEXPORT JString::operator std::string() const
    {
        return std::string(_charString);
    }

    JNIEXPORT JString::operator const char*() const
    {
        return _charString;
    }

    JNIEXPORT size_t JString::getSize() const
    {
        return strlen(_charString);
    }

    JNIEXPORT JString::operator unsigned char *() const
    {
        return (unsigned char*) _charString;
    }

}
