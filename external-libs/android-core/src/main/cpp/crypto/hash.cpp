#include "hash.h"

#include <cstdio>
#include <iomanip>
#include <sstream>

#include "openssl/md5.h"

namespace MYLIB
{
    std::string Hash::makeHashMD5(const std::string& message)
    {
        if (message.empty())
            return std::string("");

        const char* str = message.c_str();
        int length = message.size();

        unsigned char digest[MD5_DIGEST_LENGTH];
        size_t block_size = 512;

        MD5_CTX c;
        MD5_Init(&c);

        while (length > 0) {

            if (length > block_size) {

                MD5_Update(&c, str, block_size);

            } else {

                MD5_Update(&c, str, length);
            }

            length -= block_size;
        }

        MD5_Final(digest, &c);

        return convertToHex(digest, MD5_DIGEST_LENGTH);
    }

    std::string Hash::convertToHex(unsigned char const* bytes_to_encode, unsigned int in_len)
    {
        std::ostringstream os;

        for (unsigned int i = 0; i < in_len; ++i)
            os << std::hex << (int) bytes_to_encode[i];

        return std::string(os.str());
    }
}