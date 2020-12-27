#include "com_example_core_log_LogImpl.h"

#include "utils/log.h"
#include "utils/jstring.h"

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_example_core_log_LogImpl__1setTag(JNIEnv* env, jobject, jstring jtag)
{
    MYLIB::JString strTag(env, jtag);

    MYLIB::Log::setTag(strTag);
}

#ifdef __cplusplus
}
#endif