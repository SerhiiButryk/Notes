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
import com.serhii.core.security.Cipher
import com.serhii.core.security.Hash
import java.util.*

/**
 * Global point for underling C++ functionality and APIs
 */
class NativeBridge {

    val userName: String
        get() = _getUserName()

    fun verifyPassword(passwordHash: String): Boolean {
        return _verifyPassword(_getUserName(), passwordHash)
    }

    fun setNewPassword(password: String?): Boolean {
        val hash = Hash()
        return _setNewPassword(hash.hashMD5(password!!))
    }

    fun executeBlockApp() {
        _executeBlockApp()
    }

    val isAppBlocked: Boolean
        get() = _isAppBlocked()

    fun setLoginLimitFromDefault(context: Context) {
        // Get limit value from prefs
        val limit = getLockLimit(context)
        setLimitAndEnc(limit!!.toInt())
    }

    private fun setLimitAndEnc(value: Int) {
        val cipher = Cipher()
        val encMessage = cipher.encrypt(value.toString())
        // Set enc data
        _setLimitLeft(encMessage)
    }

    fun resetLoginLimitLeft(context: Context) {
        setLoginLimitFromDefault(context)
    }

    var limitLeft: Int
        get() {
            val encMessage = _getLimitLeft()
            val cipher = Cipher()
            val message = cipher.decrypt(encMessage)
            // Return result
            return message.toInt()
        }
        set(value) {
            setLimitAndEnc(value)
        }

    fun getLockLimit(context: Context): String? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val logLimitDefault = context.getString(R.string.preference_login_limit_default)
        return sharedPreferences.getString(context.getString(R.string.preference_login_limit_key), logLimitDefault)
    }

    fun createUnlockKey() {
        val cipher = Cipher()
        // Take the first 8 characters
        val randomString = cipher.getRandomString().substring(0 .. 7)
        val encMessage = cipher.encrypt(randomString)
        _setUnlockKey(encMessage)
    }

    val unlockKey: String
        get() {
            val unlockKey = _getUnlockKey()
            val cipher = Cipher()
            // Return decrypted result
            return cipher.decrypt(unlockKey)
        }

    // TODO: Check password strength
    fun checkPasswordRequirements(password: String?): Boolean {
        return !(password == null || password.isEmpty())
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