#include <jni.h>

#include "utils/log.h"
#include "utils/jstring.h"
#include "storage/system_storage.h"

using namespace MYLIB;

// TODO: Create a file with constants and remove these
const std::string K_CORE_DATA_FILE = "app.dat";
const std::string K_APP_KEY = "aRhe_)0_@Lmt";
const std::string K_APP_KEY_2 = "0oem63H-1";
const std::string K_DERIVED_KEY_2 = "02kd$qlo-1";
const std::string K_IV_BIOMETRIC = "02ldFte";
const std::string K_UNLOCK_KEY = "028lRDw";
const std::string K_APP_KEY_3 = "odqdw02";

#ifndef _Included_com_serhii_core_security_KeyMaster
#define _Included_com_serhii_core_security_KeyMaster

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_serhii_core_security_impl_KeyMaster__1save(JNIEnv *env, jobject thiz, jstring value)
{
    Info("JNI", "%s IN", __FUNCTION__ );

    JString str_value(env, value);

    SystemStorage ss;

    if (!ss.doesFileExist(K_CORE_DATA_FILE)) {
        if (!ss.createFile(K_CORE_DATA_FILE)) {
            Error("JNI", "%s failed to create a file", __FUNCTION__);
            return;
        }
    }

    if (!ss.doesKeyExist(K_CORE_DATA_FILE, K_APP_KEY)) {

        std::map<std::string, std::string> data;
        data.insert(std::make_pair(K_APP_KEY, static_cast<std::string>(str_value)));

        ss.addData(K_CORE_DATA_FILE, data);

    } else {
        Info("JNI", "%s already have this data", __FUNCTION__ );
    }


    Info("JNI", "%s OUT", __FUNCTION__ );
}

JNIEXPORT jstring JNICALL Java_com_serhii_core_security_impl_KeyMaster__1get(JNIEnv *env, jobject thiz)
{
    Info("JNI", "%s IN", __FUNCTION__ );

    SystemStorage ss;

    if (!ss.doesFileExist(K_CORE_DATA_FILE)) {
        Error("JNI", "%s file doesn't exist", __FUNCTION__ );
        return env->NewStringUTF("");
    }

    if (!ss.doesKeyExist(K_CORE_DATA_FILE, K_APP_KEY)) {
        Error("JNI", "%s value doesn't exist", __FUNCTION__ );
        return env->NewStringUTF("");
    }

    std::string value = ss.getDataByKey(K_CORE_DATA_FILE, K_APP_KEY);

    Info("JNI", "%s OUT", __FUNCTION__ );

    return env->NewStringUTF(value.c_str());
}

JNIEXPORT void JNICALL Java_com_serhii_core_security_impl_KeyMaster__1save2(JNIEnv *env, jobject thiz, jstring value)
{
    Info("JNI", "%s IN", __FUNCTION__ );

    JString str_value(env, value);

    SystemStorage ss;

    if (!ss.doesFileExist(K_CORE_DATA_FILE)) {
        if (!ss.createFile(K_CORE_DATA_FILE)) {
            Error("JNI", "%s failed to create a file", __FUNCTION__);
            return;
        }
    }

    if (!ss.doesKeyExist(K_CORE_DATA_FILE, K_APP_KEY_2)) {

        std::map<std::string, std::string> data;
        data.insert(std::make_pair(K_APP_KEY_2, static_cast<std::string>(str_value)));

        ss.addData(K_CORE_DATA_FILE, data);

    } else {
        Info("JNI", "%s already have this data", __FUNCTION__ );
    }

    Info("JNI", "%s OUT", __FUNCTION__ );
}

JNIEXPORT jstring JNICALL Java_com_serhii_core_security_impl_KeyMaster__1get2(JNIEnv *env, jobject thiz)
{
    Info("JNI", "%s IN", __FUNCTION__ );

    SystemStorage ss;

    if (!ss.doesFileExist(K_CORE_DATA_FILE)) {
        Error("JNI", "%s file doesn't exist", __FUNCTION__ );
        return env->NewStringUTF("");
    }

    if (!ss.doesKeyExist(K_CORE_DATA_FILE, K_APP_KEY_2)) {
        Error("JNI", "%s value doesn't exist", __FUNCTION__ );
        return env->NewStringUTF("");
    }

    std::string value = ss.getDataByKey(K_CORE_DATA_FILE, K_APP_KEY_2);

    Info("JNI", "%s OUT", __FUNCTION__ );

    return env->NewStringUTF(value.c_str());
}

JNIEXPORT jstring JNICALL Java_com_serhii_core_security_impl_KeyMaster__1get3(JNIEnv *env, jobject thiz)
{
    Info("JNI", "%s IN", __FUNCTION__ );

    SystemStorage ss;

    if (!ss.doesFileExist(K_CORE_DATA_FILE)) {
        Error("JNI", "%s file doesn't exist", __FUNCTION__ );
        return env->NewStringUTF("");
    }

    if (!ss.doesKeyExist(K_CORE_DATA_FILE, K_DERIVED_KEY_2)) {
        Error("JNI", "%s value doesn't exist", __FUNCTION__ );
        return env->NewStringUTF("");
    }

    std::string value = ss.getDataByKey(K_CORE_DATA_FILE, K_DERIVED_KEY_2);

    Info("JNI", "%s OUT", __FUNCTION__ );

    return env->NewStringUTF(value.c_str());
}

JNIEXPORT void JNICALL Java_com_serhii_core_security_impl_KeyMaster__1save3(JNIEnv *env, jobject thiz, jstring value)
{
    Info("JNI", "%s IN", __FUNCTION__ );

    JString str_value(env, value);

    SystemStorage ss;

    if (!ss.doesFileExist(K_CORE_DATA_FILE)) {
        if (!ss.createFile(K_CORE_DATA_FILE)) {
            Error("JNI", "%s failed to create a file", __FUNCTION__);
            return;
        }
    }

    if (!ss.doesKeyExist(K_CORE_DATA_FILE, K_DERIVED_KEY_2)) {

        std::map<std::string, std::string> data;
        data.insert(std::make_pair(K_DERIVED_KEY_2, static_cast<std::string>(str_value)));

        ss.addData(K_CORE_DATA_FILE, data);

    } else {
        Info("JNI", "%s already have this data", __FUNCTION__ );
    }

    Info("JNI", "%s OUT", __FUNCTION__ );
}

JNIEXPORT jstring JNICALL Java_com_serhii_core_security_impl_KeyMaster__1get4(JNIEnv *env, jobject thiz)
{
    Info("JNI", "%s IN", __FUNCTION__ );

    SystemStorage ss;

    if (!ss.doesFileExist(K_CORE_DATA_FILE)) {
        Error("JNI", "%s file doesn't exist", __FUNCTION__ );
        return env->NewStringUTF("");
    }

    if (!ss.doesKeyExist(K_CORE_DATA_FILE, K_IV_BIOMETRIC)) {
        Error("JNI", "%s value doesn't exist", __FUNCTION__ );
        return env->NewStringUTF("");
    }

    std::string value = ss.getDataByKey(K_CORE_DATA_FILE, K_IV_BIOMETRIC);

    Info("JNI", "%s OUT", __FUNCTION__ );

    return env->NewStringUTF(value.c_str());
}

JNIEXPORT void JNICALL Java_com_serhii_core_security_impl_KeyMaster__1save4(JNIEnv *env, jobject thiz, jstring value)
{
    Info("JNI", "%s IN", __FUNCTION__ );

    JString str_value(env, value);

    SystemStorage ss;

    if (!ss.doesFileExist(K_CORE_DATA_FILE)) {
        if (!ss.createFile(K_CORE_DATA_FILE)) {
            Error("JNI", "%s failed to create a file", __FUNCTION__);
            return;
        }
    }

    if (!ss.doesKeyExist(K_CORE_DATA_FILE, K_IV_BIOMETRIC)) {

        std::map<std::string, std::string> data;
        data.insert(std::make_pair(K_IV_BIOMETRIC, static_cast<std::string>(str_value)));

        ss.addData(K_CORE_DATA_FILE, data);

    } else {
        Info("JNI", "%s already have this data", __FUNCTION__ );
    }

    Info("JNI", "%s OUT", __FUNCTION__ );
}

JNIEXPORT jstring JNICALL Java_com_serhii_core_security_impl_KeyMaster__1get5(JNIEnv *env, jobject thiz)
{
    Info("JNI", "%s IN", __FUNCTION__ );

    SystemStorage ss;

    if (!ss.doesFileExist(K_CORE_DATA_FILE)) {
        Error("JNI", "%s file doesn't exist", __FUNCTION__ );
        return env->NewStringUTF("");
    }

    if (!ss.doesKeyExist(K_CORE_DATA_FILE, K_APP_KEY_3)) {
        Error("JNI", "%s value doesn't exist", __FUNCTION__ );
        return env->NewStringUTF("");
    }

    std::string value = ss.getDataByKey(K_CORE_DATA_FILE, K_APP_KEY_3);

    Info("JNI", "%s OUT", __FUNCTION__ );

    return env->NewStringUTF(value.c_str());
}

JNIEXPORT void JNICALL Java_com_serhii_core_security_impl_KeyMaster__1save5(JNIEnv *env, jobject thiz, jstring value)
{
    Info("JNI", "%s IN", __FUNCTION__ );

    JString str_value(env, value);

    SystemStorage ss;

    if (!ss.doesFileExist(K_CORE_DATA_FILE)) {
        if (!ss.createFile(K_CORE_DATA_FILE)) {
            Error("JNI", "%s failed to create a file", __FUNCTION__);
            return;
        }
    }

    if (!ss.doesKeyExist(K_CORE_DATA_FILE, K_APP_KEY_3)) {

        std::map<std::string, std::string> data;
        data.insert(std::make_pair(K_APP_KEY_3, static_cast<std::string>(str_value)));

        ss.addData(K_CORE_DATA_FILE, data);

    } else {
        Info("JNI", "%s already have this data", __FUNCTION__ );
    }

    Info("JNI", "%s OUT", __FUNCTION__ );
}

JNIEXPORT jstring JNICALL Java_com_serhii_core_security_impl_KeyMaster__1getUnlockKey(JNIEnv *env, jobject)
{
    Info("JNI", "%s IN", __FUNCTION__ );

    SystemStorage ss;

    if (!ss.doesFileExist(K_CORE_DATA_FILE)) {
        Error("JNI", "%s file doesn't exist", __FUNCTION__ );
        return env->NewStringUTF("");
    }

    if (!ss.doesKeyExist(K_CORE_DATA_FILE, K_UNLOCK_KEY)) {
        Error("JNI", "%s value doesn't exist", __FUNCTION__ );
        return env->NewStringUTF("");
    }

    std::string value = ss.getDataByKey(K_CORE_DATA_FILE, K_UNLOCK_KEY);

    Info("JNI", "%s OUT", __FUNCTION__ );

    return env->NewStringUTF(value.c_str());
}

JNIEXPORT void JNICALL Java_com_serhii_core_security_impl_KeyMaster__1setUnlockKey(JNIEnv *env, jobject thiz, jstring unlock_key)
{
    Info("JNI", " %s IN", __FUNCTION__ );

    JString str_value(env, unlock_key);

    SystemStorage ss;

    if (!ss.doesFileExist(K_CORE_DATA_FILE)) {
        if (!ss.createFile(K_CORE_DATA_FILE)) {
            Error("JNI", "%s failed to create a file", __FUNCTION__);
            return;
        }
    }

    if (!ss.doesKeyExist(K_CORE_DATA_FILE, K_UNLOCK_KEY)) {

        std::map<std::string, std::string> data;
        data.insert(std::make_pair(K_UNLOCK_KEY, static_cast<std::string>(str_value)));

        ss.addData(K_CORE_DATA_FILE, data);

    } else {
        Info("JNI", "%s already have this data", __FUNCTION__ );
    }

    Info("JNI", " %s OUT", __FUNCTION__ );
}

JNIEXPORT void JNICALL Java_com_serhii_core_security_impl_KeyMaster__1clear(JNIEnv *env, jobject thiz)
{
    Info("JNI", " %s IN", __FUNCTION__ );

    SystemStorage ss;

    if (!ss.doesFileExist(K_CORE_DATA_FILE)) {
        Error("JNI", "%s no file", __FUNCTION__);
        return;
    }

    ss.clearData(K_CORE_DATA_FILE);

    Info("JNI", " %s OUT", __FUNCTION__ );
}

#ifdef __cplusplus
}
#endif

#endif