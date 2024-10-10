#include "com_example_client_app_control_NativeBridge.h"

#include "app/logic/base/env_constants.h"
#include "storage/cashe_manager.h"
#include "storage/system_storage.h"
#include "utils/jstring.h"
#include "app/logic/utils/auth_utils.h"
#include "utils/log.h"

using namespace APP;
using namespace MYLIB;

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jstring JNICALL Java_com_serhii_apps_notes_control_NativeBridge__1getUserName
        (JNIEnv* env, jobject)
{
    Log::Info("JNI", " %s IN", __FUNCTION__ );

    CacheManager cacheManager;

    std::string user_name = cacheManager.getCachedData(kUserName, kFileCashSystemData);

    Log::Info("JNI", " %s OUT", __FUNCTION__ );

    return env->NewStringUTF(user_name.c_str());
}

JNIEXPORT jboolean JNICALL Java_com_serhii_apps_notes_control_NativeBridge__1verifyPassword
        (JNIEnv* env, jobject, jstring username, jstring password)
{
    Log::Info("JNI", " %s IN", __FUNCTION__ );

    JString passwordString(env, password);
    JString usernameString(env, username);

    Log::Info("JNI", " %s OUT", __FUNCTION__ );

    return static_cast<jboolean>(AuthUtils::verifyUserPassword(usernameString, passwordString));
}

JNIEXPORT jboolean JNICALL Java_com_serhii_apps_notes_control_NativeBridge__1setNewPassword
        (JNIEnv* env, jobject, jstring password)
{
    Log::Info("JNI", " %s IN", __FUNCTION__ );

    JString passwordString(env, password);

    SystemStorage ss;
    CacheManager cacheManager;

    std::string key = cacheManager.getCachedData(kUserName, kFileCashSystemData);

    Log::Info("JNI", " %s OUT", __FUNCTION__ );

    return static_cast<jboolean>(ss.updateData(kFileSystemData, key, passwordString));
}

JNIEXPORT void JNICALL Java_com_serhii_apps_notes_control_NativeBridge__1clearAppData(JNIEnv *, jobject)
{
    Log::Info("JNI", " %s IN", __FUNCTION__ );

    SystemStorage ss;

    ss.clearData(kFileCashSystemData);
    ss.clearData(kFileSystemData);

    Log::Info("JNI", " %s OUT", __FUNCTION__ );
}

JNIEXPORT jstring JNICALL Java_com_serhii_apps_notes_control_NativeBridge__1getLimitLeft
        (JNIEnv* env, jobject)
{
    Log::Info("JNI", " %s IN", __FUNCTION__ );

    SystemStorage ss;
    std::string attempts = ss.getDataByKey(kFileSystemData, kUserLoginAttemptsLeft);

    Log::Info("JNI", " %s OUT", __FUNCTION__ );

    return env->NewStringUTF(attempts.c_str());
}

JNIEXPORT void JNICALL Java_com_serhii_apps_notes_control_NativeBridge__1setLimitLeft
        (JNIEnv* env, jobject, jstring new_value)
{
    Log::Info("JNI", " %s IN", __FUNCTION__ );

    JString passwordString(env, new_value);

    SystemStorage ss;

    if (!ss.doesKeyExist(kFileSystemData, kUserLoginAttemptsLeft))
    {
        std::map<std::string, std::string> value;
        value.insert(std::make_pair(kUserLoginAttemptsLeft, static_cast<std::string>(passwordString)));

        ss.addData(kFileSystemData, value);

        Log::Info("JNI", " %s OUT", __FUNCTION__ );

        return;
    }

    ss.updateData(kFileSystemData, kUserLoginAttemptsLeft, passwordString);

    Log::Info("JNI", " %s OUT", __FUNCTION__ );

}

JNIEXPORT void JNICALL Java_com_serhii_apps_notes_control_NativeBridge__1executeBlockApp
        (JNIEnv *, jobject)
{
    Log::Info("JNI", " %s IN", __FUNCTION__ );

    SystemStorage ss;

    if (ss.doesKeyExist(kFileSystemData, kIsUserBlocked))
    {
        ss.updateData(kFileSystemData, kIsUserBlocked, TRUE);
    } else {

        std::map<std::string, std::string> value;
        value.insert(std::make_pair(kIsUserBlocked, TRUE));

        ss.addData(kFileSystemData, value);
    }

    Log::Info("JNI", " %s OUT", __FUNCTION__ );

}

JNIEXPORT jboolean JNICALL Java_com_serhii_apps_notes_control_NativeBridge__1isAppBlocked
        (JNIEnv *, jobject)
{
    Log::Info("JNI", " %s IN", __FUNCTION__ );

    SystemStorage ss;
    std::string result = ss.getDataByKey(kFileSystemData, kIsUserBlocked);

    Log::Info("JNI", " %s OUT", __FUNCTION__ );

    return static_cast<jboolean>( !(result.empty() || result == FALSE) );
}

#ifdef __cplusplus
}
#endif
