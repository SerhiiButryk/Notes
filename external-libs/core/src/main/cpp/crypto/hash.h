#pragma once

#include <string>

namespace MYLIB 
{
    std::string makeHashMD5(const std::string& message);

    std::string convertToHex(unsigned char const* bytes_to_encode, unsigned int in_len);
}
