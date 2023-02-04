#pragma once

#include <jni.h>
#include <mutex>

namespace MYLIB
{
    /**
     * Class encapsulates data needed to callback Java at the runtime
     *
     * Note: Class isn't movable and copyable
     *
     */
    class JNIWrapper
    {
        private:

            mutable std::mutex m; // Synchronizes access to isThreadAttached variable

            JavaVM* javaVm;
            JNIEnv* jniEnv;
            jobject jobj;
            jmethodID mID;

            mutable bool isThreadAttached;

        public:

            JNIEXPORT explicit JNIWrapper(JavaVM* javaVm);

            JNIEXPORT JNIWrapper(JavaVM* javaVm, jobject jobject, jmethodID mID);
            JNIEXPORT ~JNIWrapper();

            JNIEXPORT JNIWrapper(const JNIWrapper& obj) = delete;
            JNIEXPORT JNIWrapper& operator=(const JNIWrapper& obj) = delete;

            JNIEXPORT JNIWrapper(JNIWrapper&&) noexcept;
            JNIEXPORT JNIWrapper& operator=(JNIWrapper&&) noexcept;

            JNIEXPORT void setJObj(jobject jobj);
            JNIEXPORT void setMethodID(jmethodID mID);

            JNIEXPORT JNIEnv* getJniEnv() const;
            JNIEXPORT _jobject* getJobj() const;
            JNIEXPORT _jmethodID* getMethodID() const;

    private:

            JNIEnv* getSafeJniEnv() const;
    };

}
