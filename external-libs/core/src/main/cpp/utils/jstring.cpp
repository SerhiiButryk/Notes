#include "jstring.h"

#include <cstring>

namespace MYLIB
{

    JString::JString(JNIEnv* env, jstring string)
    {
        if (string != nullptr)
        {
            _charString = env->GetStringUTFChars(string, 0);
            _env = env;
            _jstring = string;
        }
    }

    JString::~JString()
    {
        if (_charString != nullptr)
        {
            _env->ReleaseStringUTFChars(_jstring, _charString);
        }
    }

    JString::operator std::string() const
    {
        return std::string(_charString);
    }

    JString::operator const char*() const
    {
        return _charString;
    }

}
