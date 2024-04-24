/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.control

import android.content.Context
import android.util.Base64
import androidx.preference.PreferenceManager
import com.serhii.apps.notes.R
import com.serhii.apps.notes.common.AppDetails.RUNTIME_LIBRARY
import com.serhii.core.log.Log
import com.serhii.core.security.Crypto
import com.serhii.core.security.Hash

/**
 * Global point for access to C++ layer
 */
object NativeBridge {

    val cryptoOpenssl = Crypto(Crypto.CRYPTO_PROVIDER_OPENSSL)
    val cryptoAndroid = Crypto(Crypto.CRYPTO_PROVIDER_ANDROID)

    val userName: String
        get() = _getUserName()

    val isAppBlocked: Boolean
        get() = _isAppBlocked()

    val unlockKey: String
        get() {
            val unlockKey = _getUnlockKey()
            // Return decrypted result
            return cryptoOpenssl.decrypt(unlockKey).message
        }

    var limitLeft: Int
        get() {
            val message = _getLimitLeft()
            val result = cryptoAndroid.decrypt(message)
            // Return result
            return result.message.toInt()
        }
        set(value) {
            val result = cryptoAndroid.encrypt(value.toString())
            // Set enc data
            _setLimitLeft(result.message)
        }

    fun verifyPassword(passwordHash: String): Boolean {
        return _verifyPassword(_getUserName(), passwordHash)
    }

    fun setNewPassword(password: String): Boolean {
        val hash = Hash()
        return _setNewPassword(hash.hashMD5(password))
    }

    fun executeBlockApp() {
        _executeBlockApp()
    }

    fun resetLoginLimitLeft(context: Context) {
        // Get limit value from prefs
        val limit = getLockLimit(context)
        if (limit != null) {
            limitLeft = limit.toInt()
        } else {
            Log.error("NativeBridge", "setLoginLimitFromDefault() failed")
        }
    }

    fun getLockLimit(context: Context): String? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val logLimitDefault = context.getString(R.string.preference_login_limit_default)
        return sharedPreferences.getString(context.getString(R.string.preference_login_limit_key), logLimitDefault)
    }

    fun createUnlockKey() {
        // Take the first 8 characters
        val randomString = cryptoOpenssl.getRandomValue(8)
        val encodedString = Base64.encode(randomString, Base64.NO_WRAP)
        val encMessage = cryptoOpenssl.encrypt(String(encodedString))
        _setUnlockKey(encMessage.message)
    }

    private external fun _getUserName(): String
    private external fun _verifyPassword(userName: String, password: String): Boolean
    private external fun _setNewPassword(password: String): Boolean
    private external fun _clearAppData()
    private external fun _getLimitLeft(): String
    private external fun _setLimitLeft(newValue: String)
    private external fun _executeBlockApp()
    private external fun _isAppBlocked(): Boolean
    private external fun _getUnlockKey(): String
    private external fun _setUnlockKey(unlockKey: String)

    init {
        System.loadLibrary(RUNTIME_LIBRARY)
    }

}