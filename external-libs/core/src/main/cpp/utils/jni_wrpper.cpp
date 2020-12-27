#include "jni_wrpper.h"

#include <string>
#include <utility>

#include "log.h"

namespace {
    // For DEBUG
    const std::string TAG = "JNIWrapper";
}

namespace MYLIB
{

    JNIWrapper::JNIWrapper(JavaVM* javaVm) : javaVm(javaVm), jobj(nullptr), mID(nullptr), isThreadAttached(false),
        jniEnv(nullptr)
    {
    }

    JNIWrapper::JNIWrapper(JavaVM* javaVm, jobject object, jmethodID mID) : javaVm(javaVm), mID(mID),
        isThreadAttached(false), jniEnv(nullptr)
    {
        setJObj(object);
    }

    JNIWrapper::JNIWrapper(JNIWrapper&& from) noexcept : javaVm(from.javaVm), jobj(from.jobj), mID(from.mID),
        isThreadAttached(std::exchange(from.isThreadAttached, false)), jniEnv(nullptr)
    {
        from.jniEnv = nullptr;
        from.javaVm = nullptr;
        from.mID = nullptr;
        from.jobj = nullptr;
    }

    JNIWrapper& JNIWrapper::operator=(JNIWrapper&& from) noexcept
    {
        if (this != &from)
        {
            javaVm = from.javaVm;
            jobj = from.jobj;
            mID = from.mID;
            isThreadAttached = from.isThreadAttached;
            jniEnv = nullptr;

            from.jniEnv = nullptr;
            from.javaVm = nullptr;
            from.mID = nullptr;
            from.jobj = nullptr;
        }

        return *this;
    }

    JNIWrapper::~JNIWrapper()
    {
        if (jobj != nullptr)
        {
            JNIEnv* jniEnv = getJniEnv();

            jniEnv->DeleteGlobalRef(jobj);
        }

        if (isThreadAttached)
        {
            javaVm->DetachCurrentThread();

            Log::Info(TAG, "~JNIWrapper(): Thread is detached %p", this);
        }

    }

    JNIEnv* JNIWrapper::getJniEnv() const
    {
        return getSafeJniEnv();
    }

    _jobject* JNIWrapper::getJobj() const
    {
        return jobj;
    }

    _jmethodID* JNIWrapper::getMethodID() const
    {
        return mID;
    }

    void JNIWrapper::setJObj(jobject jobj)
    {
        jniEnv = getSafeJniEnv();

        this->jobj = jniEnv->NewGlobalRef(jobj);
    }

    void JNIWrapper::setMethodID(jmethodID mID)
    {
        this->mID = mID;
    }

    JNIEnv* JNIWrapper::getSafeJniEnv() const
    {

        Lock lock;

        JNIEnv* jniEnv;
        int status = javaVm->GetEnv((void**) &jniEnv, JNI_VERSION_1_6);

        if (status != JNI_OK)
        {
            Log::Error(TAG, "getSafeJniEnv(): Failed to get jniEnv");
        }

        if (status == JNI_EDETACHED)
        {
            Log::Info(TAG, "getSafeJniEnv(): Thread is detached");

            int result = javaVm->AttachCurrentThread(&jniEnv, nullptr);

            if (result != JNI_OK)
            {
                Log::Error(TAG, "getSafeJniEnv(): Attach failed");

            } else {

                isThreadAttached = true;

                Log::Info(TAG, "~JNIWrapper(): Thread is attached %p", this);
            }

        }

        return jniEnv;
    }

    void callVoid(const JNIWrapper& callback)
    {
        Lock lock;

        JNIEnv* env = callback.getJniEnv();
        jobject obj = callback.getJobj();
        jmethodID mId = callback.getMethodID();

        env->CallVoidMethod(obj, mId);

        Log::Info(TAG, "callVoid(): Called callback ");
    }

}

