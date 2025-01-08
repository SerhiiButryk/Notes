#pragma once

#include <openssl/rsa.h>
#include <openssl/evp.h>
#include <openssl/rand.h>

#include <string>
#include <jni.h>
#include <iostream>
#include <iterator>
#include <cstring>
#include <random>
#include <limits>

#include "utils/log.h"

#include <core/cipher/block/aes.h>
#include <core/cipher/sym_encrypt_decrypt.h>
#include <core/cipher/sym_process_msg.h>
#include <core/cipher/sym_secret_key.h>
#include <core/cipher/sym_init_vector.h>
#include <drivers/base/traits.h>
#include <drivers/backend/openssl/config.h>
#include <util/conversions/byte.h>
#include <util/conversions/hex.h>

// TEMPORARY
//#define UNIT_TESTS

namespace MYLIB
{
    class CryptoUtils
    {
        // Log tag
        inline static const char* TAG = "";

    public:

        template <int key_length, int iv_length>
        JNIEXPORT static void symmetricEncrypt(const std::string &plaintext, std::string &cypherText, const std::string &key, const std::string &iv)
        {
            Info(TAG, "%s IN", __FUNCTION__ );

            using namespace cs_crypto::drivers::traits;
            using namespace cs_crypto::drivers;
            using namespace cs_crypto::block_cipher;
            using namespace cs_crypto::cipher;

            using openssl = driver_for<implementation::openssl>::symmetric_encryption;
            using secret_key_t = cs_crypto::cipher::secret_key<key_length>;
            using iv_t = cs_crypto::cipher::init_vector<iv_length>;

            std::vector<std::byte> ciphertext = encrypt<openssl, aes256, mode::CBC>(
                    secret_key_t::from_string(key).value(),
                    iv_t::from_string(iv).value(), plaintext).value();

            // Return hex encoded text
            cypherText.assign(cs_crypto::util::hex(ciphertext));

#ifdef UNIT_TESTS
            Info(TAG,
                  "%s\n plaintext: %s\n key (hex): %s\n iv (hex): %s\n ciphertext (hex): %s \n",
                  __FUNCTION__,
                  plaintext,
                  cs_crypto::util::hex(key),
                  cs_crypto::util::hex(iv),
                  cs_crypto::util::hex(ciphertext));
#endif
            Info(TAG, "%s OUT", __FUNCTION__ );
        }

        template <int key_length, int iv_length>
        JNIEXPORT static void symmetricDecrypt(const std::string &ciphertext, std::string &plainText, const std::string &key, const std::string &iv)
        {
            Info(TAG, "%s IN", __FUNCTION__ );

            using namespace cs_crypto::drivers::traits;
            using namespace cs_crypto::drivers;
            using namespace cs_crypto::block_cipher;
            using namespace cs_crypto::cipher;

            using openssl = driver_for<implementation::openssl>::symmetric_encryption;
            using secret_key_t = cs_crypto::cipher::secret_key<key_length>;
            using iv_t = cs_crypto::cipher::init_vector<iv_length>;

            std::vector<std::byte> ciphertextBytes = hexToBytes(ciphertext);

            std::vector<std::byte> resultData = decrypt<openssl, aes256, mode::CBC>(
                    secret_key_t::from_string(key).value(),
                    iv_t::from_string(iv).value(), ciphertextBytes).value();

            plainText.reserve(resultData.size());
            plainText.assign(resultData.size(), '\0');

            memcpy(plainText.data(), resultData.data(), resultData.size());

#ifdef UNIT_TESTS
            Info(TAG,
                  "%s\n ciphertext: %s\n key (hex): %s\n iv (hex): %s\n result: %s",
                  __FUNCTION__,
                  ciphertext,
                  cs_crypto::util::hex(key),
                  cs_crypto::util::hex(iv),
                  plainText);
#endif

            Info(TAG, "%s OUT", __FUNCTION__ );
        }

    private:
        // generate a random key and iv bytes
        template <int key_size, int block_size>
        static int generateRandomKey(unsigned char *key, unsigned char *iv)
        {
            int rc = RAND_bytes(key, key_size);
            if (rc != 1) {
                Error(TAG, "generateRandomKey() failed to gen key");
                return rc;
            }

            rc = RAND_bytes(iv, block_size);
            if (rc != 1) {
                Error(TAG, "generateRandomKey() failed to gen iv");
                return rc;
            }

            Info(TAG, "generateRandomKey() success");
            return rc;
        }

        // generate a random string with length of key_size
        template <int key_size>
        static std::string randomStr() noexcept
        {
            using namespace cs_crypto::drivers::traits;
            using namespace cs_crypto::drivers;
            using namespace cs_crypto::block_cipher;
            using namespace cs_crypto::cipher;

            std::string retval;
            retval.reserve(key_size);

            std::random_device inputDevice;
            std::mt19937 generateEngine(inputDevice());
            std::uniform_int_distribution<short> outputRange(0, std::numeric_limits<short>::max() - 1);

            for (int i = 0; i < key_size / 2; ++i) {
                short val = outputRange(generateEngine);

                retval.push_back(val & 0xFF);
                retval.push_back((val >> 4) & 0xFF);
            }

            return retval;
        }

        // convert hex to byte array
        static std::vector<std::byte> hexToBytes(const std::string& hex);

    };

}
