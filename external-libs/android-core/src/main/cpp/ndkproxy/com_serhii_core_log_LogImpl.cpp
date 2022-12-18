#include "com_serhii_core_log_LogImpl.h"

#include "utils/log.h"
#include "utils/jstring.h"

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_serhii_core_log_LogImpl__1setTag(JNIEnv* env, jobject, jstring jtag)
{
    MYLIB::JString strTag(env, jtag);
    MYLIB::Log::setTag(strTag);
}

JNIEXPORT void JNICALL Java_com_serhii_core_log_LogImpl__1setDetailLog(JNIEnv *env, jobject thiz, jboolean enable)
{
    MYLIB::Log::setIsDetailedLogsEnabled(enable);
}

JNIEXPORT jboolean JNICALL Java_com_serhii_core_log_LogImpl__1getDetailLog(JNIEnv *env, jobject thiz)
{
    return MYLIB::Log::isDetailedLogsEnabled();
}

JNIEXPORT void JNICALL
Java_com_serhii_core_log_LogImpl__1enableDetailLogForDebug(JNIEnv *env, jobject thiz)
{
    jclass cls = env->FindClass("com/serhii/apps/notes/BuildConfig");
    if (cls) {
        jfieldID fid = env->GetStaticFieldID(cls, "DEBUG", "Z");
        if (fid) {
            jboolean res = env->GetStaticBooleanField(cls, fid);
            if (res) {
                MYLIB::Log::setIsDetailedLogsEnabled(true);
            }
        }
    }
}

#ifdef __cplusplus
}
#endif