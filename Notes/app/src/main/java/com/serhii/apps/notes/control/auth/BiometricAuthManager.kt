/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.control.auth

import com.serhii.core.log.Log.Companion.info
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import com.serhii.apps.notes.R
import android.content.pm.PackageManager
import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.Fragment

class BiometricAuthManager {

    private var biometricPrompt: BiometricPrompt? = null
    private var promptInfo: PromptInfo? = null
    private var listener: OnAuthenticateListener? = null

    fun setOnAuthenticateSuccess(listener: OnAuthenticateListener?) {
        this.listener = listener
    }

    fun initBiometricSettings(context: Context, fragment: Fragment?) {
        val executor = ContextCompat.getMainExecutor(context)

        biometricPrompt = BiometricPrompt(
            fragment!!,
            executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    info(TAG, "onAuthenticationError() : $errorCode $errString")
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    if (listener != null) listener!!.onSuccess()
                    info(TAG, "onAuthenticationSucceeded()")
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    info(TAG, "onAuthenticationFailed()")
                }
            })

        promptInfo = PromptInfo.Builder()
            .setTitle(context.getString(R.string.biometric_prompt_title))
            .setSubtitle(context.getString(R.string.biometric_prompt_subtitle)) // User can authenticate with device PIN/Password
            .setDeviceCredentialAllowed(true)
            .build()
    }

    fun authenticate() {
        biometricPrompt!!.authenticate(promptInfo!!)
    }

    fun hasFingerPrint(context: Context): Boolean {
        val pm = context.packageManager
        return pm.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
    }

    fun canAuthenticate(context: Context?): Boolean {
        val biometricManager = BiometricManager.from(
            context!!
        )
        val result = biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS
        if (result) {
            info(TAG, "Biometric are available")
        } else {
            info(TAG, "Biometric are NOT available (NOT enrolled or device doesn't support it)")
        }
        return result
    }

    interface OnAuthenticateListener {
        fun onSuccess()
    }

    companion object {
        private const val TAG = "BiometricAuthManager"
        private const val UNLOCK_KEYSTORE_REQUEST_CODE = 200

        fun requestUnlockActivity(activity: Activity) {
            val keyguardManager =
                activity.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            val intent = keyguardManager.createConfirmDeviceCredentialIntent(null, null)
            if (intent != null) {
                activity.startActivityForResult(intent, UNLOCK_KEYSTORE_REQUEST_CODE)
            }
        }

        fun isUnlockActivityResult(requestCode: Int, resultCode: Int): Boolean {
            return requestCode == UNLOCK_KEYSTORE_REQUEST_CODE && resultCode == Activity.RESULT_OK
        }
    }
}