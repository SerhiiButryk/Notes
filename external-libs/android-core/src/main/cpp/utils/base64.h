#pragma once

#include <string>
#include <jni.h>

namespace MYLIB
{

    class Base64 
    { 
        public:

            JNIEXPORT static std::string encode(unsigned char const* bytes_to_encode, unsigned int in_length);

            JNIEXPORT static std::string decode(const std::string& encoded_string);
    };
    
}
