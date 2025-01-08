#include "jni_utils.h"

inline static const char* TAG = "JniUtils";

namespace MYLIB
{
    JNIEXPORT void JniUtils::callVoid(const JNIWrapper &callback)
    {
        Info(TAG, "callVoid(): Call callback without arguments");

        std::lock_guard<std::mutex> guard(m);

        JNIEnv *env = callback.getJniEnv();
        jobject obj = callback.getJobj();
        jmethodID mId = callback.getMethodID();

        env->CallVoidMethod(obj, mId);
    }

    JNIEXPORT std::mutex JniUtils::m;
}
