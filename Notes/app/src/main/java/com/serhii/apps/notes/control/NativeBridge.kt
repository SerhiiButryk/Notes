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
import com.serhii.core.security.Crypto
import com.serhii.core.security.Hash

/**
 * Global point for access to C++ layer
 */
object NativeBridge {

    val crypto = Crypto()

    val userName: String
        get() = _getUserName()

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

    val isAppBlocked: Boolean
        get() = _isAppBlocked()

    fun setLoginLimitFromDefault(context: Context) {
        // Get limit value from prefs
        val limit = getLockLimit(context)
        val encMessage = crypto.encrypt(limit.toString())
        // Set enc data
        _setLimitLeft(encMessage)
    }

    fun resetLoginLimitLeft(context: Context) {
        setLoginLimitFromDefault(context)
    }

    var limitLeft: Int
        get() {
            val encMessage = _getLimitLeft()
            val message = crypto.decrypt(encMessage)
            // Return result
            return message.toInt()
        }
        private set(value) {
            val encMessage = crypto.encrypt(value.toString())
            // Set enc data
            _setLimitLeft(encMessage)
        }

    fun updateLimit(value: Int) {
        limitLeft = value
    }

    fun getLockLimit(context: Context): String? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val logLimitDefault = context.getString(R.string.preference_login_limit_default)
        return sharedPreferences.getString(context.getString(R.string.preference_login_limit_key), logLimitDefault)
    }

    fun createUnlockKey() {
        // Take the first 8 characters
        val randomString = crypto.getRandomValue(8)
        val encodedString = Base64.encode(randomString, Base64.NO_WRAP)
        val encMessage = crypto.encrypt(String(encodedString))
        _setUnlockKey(encMessage)
    }

    val unlockKey: String
        get() {
            val unlockKey = _getUnlockKey()
            // Return decrypted result
            return crypto.decrypt(unlockKey)
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