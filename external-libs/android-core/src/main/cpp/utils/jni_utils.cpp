#include "jni_utils.h"

namespace MYLIB
{
    void JniUtils::callVoid(const JNIWrapper &callback)
    {
        Log::Info(TAG, "callVoid(): Call callback without arguments");

        std::lock_guard<std::mutex> guard(m);

        JNIEnv *env = callback.getJniEnv();
        jobject obj = callback.getJobj();
        jmethodID mId = callback.getMethodID();

        env->CallVoidMethod(obj, mId);
    }

    std::mutex JniUtils::m;
    const std::string JniUtils::TAG = "JniUtils";
}
