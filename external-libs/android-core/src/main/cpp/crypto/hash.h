#pragma once

#include <string>
#include <jni.h>

namespace MYLIB 
{
    class Hash
    {
    public:
        JNIEXPORT static std::string makeHashMD5(const std::string &message);

    private:
        JNIEXPORT static std::string convertToHex(unsigned char const *bytes_to_encode, unsigned int in_len);

    };
}
