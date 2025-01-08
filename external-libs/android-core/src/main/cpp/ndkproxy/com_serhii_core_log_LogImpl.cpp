#include "com_serhii_core_log_LogImpl.h"

#include "utils/log.h"
#include "utils/jstring.h"

#ifdef __cplusplus
extern "C" {
#endif

bool isDebug(JNIEnv *env) {

    jclass cls = env->FindClass("com/serhii/apps/notes/BuildConfig");

    if (env->ExceptionCheck()) {
        env->ExceptionClear();
        return false;
    }

    if (cls) {
        jfieldID fid = env->GetStaticFieldID(cls, "DEBUG", "Z");
        if (fid) {
            jboolean res = env->GetStaticBooleanField(cls, fid);
            return res;
        }
    }

    return false;
}

JNIEXPORT void JNICALL Java_com_serhii_core_log_LogImpl__1setTag(JNIEnv* env, jobject, jstring jtag)
{
    MYLIB::JString strTag(env, jtag);
//    MYLIB::Log::setTag(strTag);
}

JNIEXPORT void JNICALL Java_com_serhii_core_log_LogImpl__1setDetailLog(JNIEnv *env, jobject thiz, jboolean enable)
{
    if (isDebug(env)) {
        MYLIB::Log::setDetailedLogsEnabled(true);
    } else {
        MYLIB::Log::setDetailedLogsEnabled(enable);
    }
}

JNIEXPORT jboolean JNICALL Java_com_serhii_core_log_LogImpl__1isDetailLogEnabled(JNIEnv *env, jobject thiz)
{
    return MYLIB::Log::isDetailedLogsEnabled();
}

JNIEXPORT void JNICALL
Java_com_serhii_core_log_LogImpl__1enableDetailLogIfDebug(JNIEnv *env, jobject thiz)
{
    if (isDebug(env)) {
        MYLIB::Log::setDetailedLogsEnabled(true);
    }
}

#ifdef __cplusplus
}
#endif