/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.security

import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.serhii.core.CoreEngine
import com.serhii.core.log.Log
import com.serhii.core.security.impl.crypto.AndroidProvider
import javax.crypto.spec.IvParameterSpec

/**
 * Class which provides APIs for Biometric authentication
 */
class BiometricAuthenticator {

    private var biometricPrompt: BiometricPrompt? = null
    private var promptInfo: BiometricPrompt.PromptInfo? = null
    private var listener: Listener? = null

    fun authenticateInitial(listener: Listener) {
        if (biometricPrompt == null || promptInfo == null) {
            Log.error(TAG, "authenticateInitial(): prompt is null, return")
            return
        }

        this.listener = listener

        val cipher = getCipherForEncryption()
        val iv = cipher.iv

        CoreEngine.getKeyMaster().saveIVForBiometric(iv)

        biometricPrompt!!.authenticate(promptInfo!!, BiometricPrompt.CryptoObject(cipher))
    }

    fun authenticate(listener: Listener) {
        if (biometricPrompt == null || promptInfo == null) {
            Log.error(TAG, "authenticate(): prompt is null, return")
            return
        }

        this.listener = listener

        val iv = CoreEngine.getKeyMaster().getIVForBiometric()

        val cipher = getCipherForDecryption(iv)
        biometricPrompt!!.authenticate(promptInfo!!, BiometricPrompt.CryptoObject(cipher))
    }

    fun init(context: Context, fragment: FragmentActivity, title: String, subtitle: String, buttonText: String) {

        val executor = ContextCompat.getMainExecutor(context)

        biometricPrompt = BiometricPrompt(
            fragment,
            executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    Log.error(TAG, "onAuthenticationError() : $errorCode $errString")
                    super.onAuthenticationError(errorCode, errString)
                    listener?.onFailure()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    Log.info(TAG, "onAuthenticationSucceeded()")
                    super.onAuthenticationSucceeded(result)
                    val cipher = result.cryptoObject?.cipher
                    if (cipher != null && listener != null) {
                        listener?.onSuccess(cipher)
                    } else {
                        listener?.onFailure()
                    }
                }

                override fun onAuthenticationFailed() {
                    Log.error(TAG, "onAuthenticationFailed()")
                    super.onAuthenticationFailed()
                    listener?.onFailure()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText(buttonText)
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()
    }

    companion object {

        fun isReady(): Boolean {
            return CoreEngine.getKeyMaster().biometricsInitialized()
        }

        fun biometricsAvailable(context: Context): Boolean {
            return canAuthenticate(context) && hasFingerPrint(context)
        }

        private fun hasFingerPrint(context: Context): Boolean {
            val pm = context.packageManager
            return pm.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
        }

        private fun canAuthenticate(context: Context): Boolean {
            val biometricManager = BiometricManager.from(context)
            val result = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
            if (result) {
                Log.info(TAG, "Biometric are available")
            } else {
                Log.info(TAG, "Biometric are NOT available (NOT enrolled or device doesn't support it)")
            }
            return result
        }

        private fun getCipherForDecryption(initializationVector: ByteArray): javax.crypto.Cipher {

            val provider = CoreEngine.configure(null, Crypto.CRYPTO_PROVIDER_ANDROID)

            val secretKey = (provider as AndroidProvider).getSecretKeyForBiometricAuthOrCreate()
            val cipher = provider.getCipherForBiometricAuth()

            cipher.init(
                javax.crypto.Cipher.DECRYPT_MODE,
                secretKey,
                IvParameterSpec(initializationVector)
            )
            return cipher
        }

        private fun getCipherForEncryption(): javax.crypto.Cipher {

            val provider = CoreEngine.configure(null, Crypto.CRYPTO_PROVIDER_ANDROID)

            val secretKey = (provider as AndroidProvider).getSecretKeyForBiometricAuthOrCreate()
            val cipher = provider.getCipherForBiometricAuth()

            cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, secretKey)
            return cipher
        }

        private const val TAG = "BiometricAuthenticator"

    }

    interface Listener {
        fun onSuccess(cipher: javax.crypto.Cipher)
        fun onFailure()
    }

}