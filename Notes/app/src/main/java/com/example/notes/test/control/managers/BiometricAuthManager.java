package com.example.notes.test.control.managers;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.core.common.log.Log;

import java.util.concurrent.Executor;

public class BiometricAuthManager {

    private final static String TAG = "BiometricAuthManager";

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    private OnAuthenticateListener listener;

    public void setOnAuthenticateListener(OnAuthenticateListener listener) {
        this.listener = listener;
    }

    public void initBiometricSettings(Context context, Fragment fragment) {

        executor = ContextCompat.getMainExecutor(context);

        biometricPrompt = new BiometricPrompt(fragment,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);

                Log.info(TAG, "onAuthenticationError() : " + errorCode + " " + errString);
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                if (listener != null)
                    listener.onSuccess();

                Log.info(TAG, "onAuthenticationSucceeded()");
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();

                Log.info(TAG, "onAuthenticationFailed()");
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build();

    }

    public void authenticate() {
        biometricPrompt.authenticate(promptInfo);
    }

    public boolean hasFingerPrint(Context context) {
        final PackageManager pm = context.getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT);
    }

    public boolean canAuthenticate(Context context) {
        BiometricManager biometricManager = BiometricManager.from(context);
        return (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS);
    }

    public interface OnAuthenticateListener {

        void onSuccess();
    }

}
