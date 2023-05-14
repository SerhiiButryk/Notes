#pragma once

#include "jni_wrpper.h"
#include "log.h"

#include <typeinfo>

namespace MYLIB
{

    class JniUtils
    {
    public:
        /**
         *  Helper method to callback Java level
         */
        JNIEXPORT static void callVoid(const JNIWrapper& callback);

        /**
         *  Helper method to callback Java level
         */
        template<typename T>
        JNIEXPORT static void callVoid(const JNIWrapper& callback, T arg)
        {
            Log::Info(TAG, "callVoid(): Call callback with argument of type %s ", typeid(arg).name());

            std::lock_guard<std::mutex> guard(m);

            JNIEnv* env = callback.getJniEnv();
            jobject obj = callback.getJobj();
            jmethodID mId = callback.getMethodID();

            env->CallVoidMethod(obj, mId, arg);
        }

    private:

        static std::mutex m; // Access protection

        static const std::string TAG;

    };

}

