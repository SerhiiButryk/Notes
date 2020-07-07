#ifndef CRYPTO_BASE64_H
#define CRYPTO_BASE64_H

/** Implementation copied from :
/** https://renenyffenegger.ch/notes/development/Base64/Encoding-and-decoding-base-64-with-cpp **/

#include <string>

namespace MYLIB 
{
    std::string encode64(unsigned char const* bytes_to_encode, unsigned int in_len);
    
    std::string decode64(const std::string& encoded_string);
}

#endif
