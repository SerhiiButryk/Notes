#pragma once

#include <jni.h>
#include <mutex>

/**
 *  Class wraps up data needed for Java callback at the runtime
 *
 */

namespace MYLIB
{

    class Lock
    {
        private:
            std::mutex m;
            std::unique_lock<std::mutex> l;

        public:
            Lock();
            ~Lock();

            Lock(const Lock&) = delete;
            Lock& operator=(const Lock&) = delete;

            Lock(Lock&&) = delete;
            Lock& operator=(Lock&&) = delete;
    };

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

            JNIWrapper(JavaVM* javaVm);
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

    /**
     *  Helper functions for Java function invocation
     */
    void callVoid(const JNIWrapper& callback);

    template<typename T>
    void callVoid(const JNIWrapper& callback, T arg)
    {
        Lock lock;

        JNIEnv* env = callback.getJniEnv();
        jobject obj = callback.getJobj();
        jmethodID mId = callback.getMethodID();

        env->CallVoidMethod(obj, mId, arg);
    }

}
