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

            explicit JNIWrapper(JavaVM* javaVm);

            JNIWrapper(JavaVM* javaVm, jobject jobject, jmethodID mID);
            ~JNIWrapper();

            JNIWrapper(const JNIWrapper& obj) = delete;
            JNIWrapper& operator=(const JNIWrapper& obj) = delete;

            JNIWrapper(JNIWrapper&&) noexcept;
            JNIWrapper& operator=(JNIWrapper&&) noexcept;

            void setJObj(jobject jobj);
            void setMethodID(jmethodID mID);

            JNIEnv* getJniEnv() const;
            _jobject* getJobj() const;
            _jmethodID* getMethodID() const;

    private:

            JNIEnv* getSafeJniEnv() const;
    };

}
