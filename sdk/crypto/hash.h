#ifndef _HASH_H_
#define _HASH_H_

#include <string>

namespace MYLIB 
{
    std::string makeHashMD5(const std::string& message);

    std::string convertToHex(unsigned char const* bytes_to_encode, unsigned int in_len);
}

#endif