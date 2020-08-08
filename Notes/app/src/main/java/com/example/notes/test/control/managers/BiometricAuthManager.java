package com.example.notes.test.control.managers;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.core.common.log.Log;

import java.util.concurrent.Executor;

import static android.app.Activity.RESULT_OK;

public class BiometricAuthManager {

    private final static String TAG = "BiometricAuthManager";

    private static final int UNLOCK_KEYSTORE_REQUEST_CODE = 200;

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
                .setTitle("Biometric login for my app") // TODO Move to xml
                .setSubtitle("Log in using your biometric credential") // TODO Move to xml
                .setDeviceCredentialAllowed(true)
                .build();

    }

    public static void requestUnlockActivity(Activity activity) {
        KeyguardManager keyguardManager = (KeyguardManager) activity.getSystemService(Context.KEYGUARD_SERVICE);

        Intent intent = keyguardManager.createConfirmDeviceCredentialIntent(null, null);

        if (intent != null) {
            activity.startActivityForResult(intent, UNLOCK_KEYSTORE_REQUEST_CODE);
        }
    }

    public static boolean isUnlockActivityResult(int requestCode, int resultCode) {
        return requestCode == UNLOCK_KEYSTORE_REQUEST_CODE && resultCode == RESULT_OK;
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
