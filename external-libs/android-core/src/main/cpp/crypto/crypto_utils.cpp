#include "crypto_utils.h"

namespace MYLIB {

    const std::string CryptoUtils::TAG = "CryptoUtils";

    std::vector<std::byte> CryptoUtils::hexToBytes(const std::string& hex)
    {
        std::vector<std::byte> bytes;

        for (unsigned int i = 0; i < hex.length(); i += 2) {
            std::string byteString = hex.substr(i, 2);
            auto _byte = (std::byte) strtol(byteString.c_str(), nullptr, 16);
            bytes.push_back(_byte);
        }

        return bytes;
    }

}
