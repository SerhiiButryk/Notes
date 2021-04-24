#pragma once

#include <string>

namespace MYLIB
{

    class Base64 
    { 
        public:

            static std::string encode(unsigned char const* bytes_to_encode, unsigned int in_length);
            
            static std::string decode(const std::string& encoded_string);
    };
    
}
