/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.control

import android.content.Context
import android.util.Base64
import androidx.preference.PreferenceManager
import com.serhii.apps.notes.R
import com.serhii.apps.notes.common.AppConstants
import com.serhii.apps.notes.common.AppConstants.RUNTIME_LIBRARY
import com.serhii.apps.notes.database.Keys
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
        cipher.selectKey(Keys.SECRET_KEY_PASSWORD_ENC_ALIAS)
        val (message, iv1) = cipher.encryptSymmetric(value.toString())
        val iv = String(Base64.encode(iv1, Base64.NO_WRAP))
        val encMessage = message + LOG_LIMIT_MARKER + iv
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
            val encLimit = encMessage.split(LOG_LIMIT_MARKER).toTypedArray()[0]
            val encIv = encMessage.split(LOG_LIMIT_MARKER).toTypedArray()[1]
            val iv = Base64.decode(encIv.toByteArray(), Base64.NO_WRAP)
            val cipher = Cipher()
            cipher.selectKey(Keys.SECRET_KEY_PASSWORD_ENC_ALIAS)
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
        var unlockKey = UUID.randomUUID().toString()
        // Take the first 8 characters
        unlockKey = unlockKey.substring(0, unlockKey.indexOf('-'))
        // Encrypt value
        val cipher = Cipher()
        cipher.selectKey(Keys.SECRET_KEY_PASSWORD_ENC_ALIAS)
        val (message, iv1) = cipher.encryptSymmetric(unlockKey)
        val iv = String(Base64.encode(iv1, Base64.NO_WRAP))
        val encMessage = message + LOG_UNLOCK_KEY_MARKER + iv
        _setUnlockKey(encMessage)
    }

    // Decrypt value
    val unlockKey: String
        // Return result
        get() {
            val unlockKey = _getUnlockKey()
            // Decrypt value
            val encLimit = unlockKey.split(LOG_UNLOCK_KEY_MARKER).toTypedArray()[0]
            val encIv = unlockKey.split(LOG_UNLOCK_KEY_MARKER).toTypedArray()[1]
            val iv = Base64.decode(encIv.toByteArray(), Base64.NO_WRAP)
            val cipher = Cipher()
            cipher.selectKey(Keys.SECRET_KEY_PASSWORD_ENC_ALIAS)
            val (message) = cipher.decryptSymmetric(encLimit, iv)
            // Return result
            return message
        }

    // TODO: Check password strength
    fun checkPasswordRequirements(password: String?): Boolean {
        return if (password == null || password.isEmpty()) false else true
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

    companion object {
        private const val LOG_LIMIT_MARKER = "SSDD" // Random value
        private const val LOG_UNLOCK_KEY_MARKER = "AALL" // Random value

        init {
            System.loadLibrary(RUNTIME_LIBRARY)
        }
    }
}