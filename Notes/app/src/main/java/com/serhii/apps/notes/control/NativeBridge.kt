/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.control

import android.content.Context
import android.util.Base64
import androidx.preference.PreferenceManager
import com.serhii.apps.notes.R
import com.serhii.apps.notes.common.AppConstants.RUNTIME_LIBRARY
import com.serhii.core.security.Cipher
import com.serhii.core.security.Hash
import java.util.*

/**
 * Class accessor for underling C++ APIs
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
        // Encrypt value
        val cipher = Cipher()
        val (message, iv1) = cipher.encryptSymmetric(value.toString())
        val iv = String(Base64.encode(iv1, Base64.NO_WRAP))
        // Iv should have 16 length
        if (iv.length != 16) {
            throw RuntimeException("Failed to set limit. Expected length: 16, actual length: ${iv.length}")
        }
        val encMessage = iv + message
        // Pass enc data
        _setLimitLeft(encMessage)
    }

    fun resetLoginLimitLeft(context: Context) {
        setLoginLimitFromDefault(context)
    }

    var limitLeft: Int
        get() {
            val encMessage = _getLimitLeft()
            // Decrypt value
            val encLimit = encMessage.substring(16)
            val encIv = encMessage.substring(0, 16)
            val iv = Base64.decode(encIv.toByteArray(), Base64.NO_WRAP)
            val cipher = Cipher()
            val (message) = cipher.decryptSymmetric(encLimit, iv)
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
        var randomString = UUID.randomUUID().toString()
        // Take the first 8 characters
        randomString = randomString.substring(0, 8)
        // Encrypt value
        val cipher = Cipher()
        val (message, iv1) = cipher.encryptSymmetric(randomString)
        val iv = String(Base64.encode(iv1, Base64.NO_WRAP))
        // Iv should have 16 length
        if (iv.length != 16) {
            throw RuntimeException("Failed to create unlock key. Expected length: 16, actual length: ${iv.length}")
        }
         val encMessage = iv + message
        _setUnlockKey(encMessage)
    }

    val unlockKey: String
        get() {
            val unlockKey = _getUnlockKey()
            val iv = unlockKey.substring(0, 16)
            val encMessage = unlockKey.substring(16)
            // Decrypt value
            val ivDecoded = Base64.decode(iv.toByteArray(), Base64.NO_WRAP)
            val cipher = Cipher()
            // Return decrypted result
            return cipher.decryptSymmetric(encMessage, ivDecoded).message
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