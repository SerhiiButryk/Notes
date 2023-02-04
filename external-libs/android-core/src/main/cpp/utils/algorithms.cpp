#include "algorithms.h"

#include <algorithm>

namespace MYLIB
{

    JNIEXPORT bool Algorithms::containSpace(const std::string& str)
    {
        auto res = std::find_if(str.begin(), str.end(), [](char c) { return c == ' '; });

        return res != str.end();
    }

}