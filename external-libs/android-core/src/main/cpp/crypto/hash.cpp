#include "hash.h"

#include <cstdio>
#include <iomanip>
#include <sstream>

#include "openssl/md5.h"
#include "utils/log.h"

namespace MYLIB
{
    std::string Hash::makeHashMD5(const std::string& message)
    {
        Log::Info("Hash", "makeHashMD5(), IN");

        if (message.empty()) {
            Log::Info("Hash", "makeHashMD5(), empty message");
            return std::string("");
        }

        size_t length = message.size();
        size_t blockSize = 50;
        size_t counter = 0;
        unsigned char digest[MD5_DIGEST_LENGTH];
        MD5_CTX ctx;
        MD5_Init(&ctx);

        // Case when input message length are more then block size value
        while (length > blockSize && length > counter) {
            size_t endPos = counter + blockSize;
            std::string subString = std::string(message, counter, endPos);
            MD5_Update(&ctx, subString.c_str(), subString.size());
            counter += endPos;
        }

        // Case when input message length are less then block size value
        if (counter == 0) {
            MD5_Update(&ctx, message.c_str(), message.size());
        }

        MD5_Final(digest, &ctx);

        Log::Info("Hash", "makeHashMD5(), OUT");

        return  convertToHex(digest, MD5_DIGEST_LENGTH);
    }

    std::string Hash::convertToHex(unsigned char const* bytes_to_encode, unsigned int in_len)
    {
        std::ostringstream os;

        for (unsigned int i = 0; i < in_len; ++i)
            os << std::hex << std::setw(2) << std::setfill('0') << (int) bytes_to_encode[i];

        return std::string(os.str());
    }
}