#include "com_example_client_app_control_NativeBridge.h"

#include "app/logic/base/env_constants.h"
#include "storage/cashe_manager.h"
#include "storage/system_storage.h"
#include "utils/jstring.h"
#include "app/logic/utils/auth_utils.h"

using namespace APP;
using namespace MYLIB;

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jstring JNICALL Java_com_serhii_apps_notes_control_NativeBridge__1getUserName
        (JNIEnv* env, jobject)
{
    CacheManager cacheManager;

    std::string user_name = cacheManager.getCachedValue(kUserName, kFileCashSystemData);

    return env->NewStringUTF(user_name.c_str());
}

JNIEXPORT jboolean JNICALL Java_com_serhii_apps_notes_control_NativeBridge__1verifyPassword
        (JNIEnv* env, jobject, jstring username, jstring password)
{
    JString passwordString(env, password);
    JString usernameString(env, username);

    return static_cast<jboolean>(AuthUtils::verifyUserPassword(usernameString, passwordString));
}

JNIEXPORT jboolean JNICALL Java_com_serhii_apps_notes_control_NativeBridge__1setNewPassword
        (JNIEnv* env, jobject, jstring password)
{
    JString passwordString(env, password);

    SystemStorage ss;
    CacheManager cacheManager;

    std::string key = cacheManager.getCachedValue(kUserName, kFileCashSystemData);

    return static_cast<jboolean>(ss.updateValue(kFileSystemData, key, passwordString));
}

JNIEXPORT void JNICALL Java_com_serhii_apps_notes_control_NativeBridge__1clearAppData(JNIEnv *, jobject)
{
    SystemStorage ss;

    ss.clearFileData(kFileCashSystemData);
    ss.clearFileData(kFileSystemData);
}

JNIEXPORT jstring JNICALL Java_com_serhii_apps_notes_control_NativeBridge__1getLimitLeft
        (JNIEnv* env, jobject)
{
    SystemStorage ss;
    std::string attempts = ss.getValueByKey(kFileSystemData, kUserLoginAttemptsLeft);

    return env->NewStringUTF(attempts.c_str());
}

JNIEXPORT void JNICALL Java_com_serhii_apps_notes_control_NativeBridge__1setLimitLeft
        (JNIEnv* env, jobject, jstring new_value)
{
    JString passwordString(env, new_value);

    SystemStorage ss;

    if (!ss.doesKeyExist(kFileSystemData, kUserLoginAttemptsLeft))
    {
        std::map<std::string, std::string> value;
        value.insert(std::make_pair(kUserLoginAttemptsLeft, static_cast<std::string>(passwordString)));

        ss.addValues(kFileSystemData, value);

        return;
    }

    ss.updateValue(kFileSystemData, kUserLoginAttemptsLeft, passwordString);
}

JNIEXPORT void JNICALL Java_com_serhii_apps_notes_control_NativeBridge__1executeBlockApp
        (JNIEnv *, jobject)
{
    SystemStorage ss;

    if (ss.doesKeyExist(kFileSystemData, kIsUserBlocked))
    {
        ss.updateValue(kFileSystemData, kIsUserBlocked, TRUE);
    } else {

        std::map<std::string, std::string> value;
        value.insert(std::make_pair(kIsUserBlocked, TRUE));

        ss.addValues(kFileSystemData, value);
    }

}

JNIEXPORT jboolean JNICALL Java_com_serhii_apps_notes_control_NativeBridge__1isAppBlocked
        (JNIEnv *, jobject)
{
    SystemStorage ss;
    std::string result =  ss.getValueByKey(kFileSystemData, kIsUserBlocked);

    return static_cast<jboolean>( !(result.empty() || result == FALSE) );
}

JNIEXPORT jstring JNICALL Java_com_serhii_apps_notes_control_NativeBridge__1getUnlockKey
        (JNIEnv* env, jobject)
{
    return env->NewStringUTF(UNLOCK_ACCESSKEY_DEFAULT.c_str());
}

#ifdef __cplusplus
}
#endif
