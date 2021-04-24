#pragma once

#include <string>

namespace MYLIB 
{
    class Hash
    {
    public:
        static std::string makeHashMD5(const std::string &message);

    private:
        static std::string convertToHex(unsigned char const *bytes_to_encode, unsigned int in_len);

    };
}
