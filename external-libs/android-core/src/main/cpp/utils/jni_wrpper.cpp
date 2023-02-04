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

    JNIEXPORT JNIWrapper::JNIWrapper(JavaVM* javaVm) : javaVm(javaVm), jobj(nullptr), mID(nullptr), isThreadAttached(false),
        jniEnv(nullptr)
    {
    }

    JNIEXPORT JNIWrapper::JNIWrapper(JavaVM* javaVm, jobject object, jmethodID mID) : javaVm(javaVm), mID(mID),
        isThreadAttached(false), jniEnv(nullptr)
    {
        setJObj(object);
    }

    JNIEXPORT JNIWrapper::JNIWrapper(JNIWrapper&& from) noexcept : javaVm(from.javaVm), jobj(from.jobj), mID(from.mID),
        isThreadAttached(std::exchange(from.isThreadAttached, false)), jniEnv(nullptr)
    {
        from.jniEnv = nullptr;
        from.javaVm = nullptr;
        from.mID = nullptr;
        from.jobj = nullptr;
    }

    JNIEXPORT JNIWrapper& JNIWrapper::operator=(JNIWrapper&& from) noexcept
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

    JNIEXPORT JNIWrapper::~JNIWrapper()
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

    JNIEXPORT JNIEnv* JNIWrapper::getJniEnv() const
    {
        return getSafeJniEnv();
    }

    JNIEXPORT _jobject* JNIWrapper::getJobj() const
    {
        return jobj;
    }

    JNIEXPORT _jmethodID* JNIWrapper::getMethodID() const
    {
        return mID;
    }

    JNIEXPORT void JNIWrapper::setJObj(jobject jobj)
    {
        jniEnv = getSafeJniEnv();

        this->jobj = jniEnv->NewGlobalRef(jobj);
    }

    JNIEXPORT void JNIWrapper::setMethodID(jmethodID mID)
    {
        this->mID = mID;
    }

    JNIEnv* JNIWrapper::getSafeJniEnv() const
    {
        std::lock_guard<std::mutex> guard(m);

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

}

