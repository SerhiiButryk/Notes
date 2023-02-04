#pragma once

#include <string>
#include <jni.h>

namespace MYLIB
{

    class Algorithms
    {
        public:
            JNIEXPORT static bool containSpace(const std::string& str);
    };

}
