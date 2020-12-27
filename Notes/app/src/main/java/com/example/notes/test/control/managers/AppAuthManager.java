package com.example.notes.test.control.managers;

import com.example.notes.test.control.types.AuthorizeType;
import com.example.notes.test.ui.data_model.AuthModel;
import com.example.notes.test.control.types.RequestType;

import static com.example.notes.test.common.AppConstants.RUNTIME_LIBRARY;

/**
 *  Performs user authorization request processing
 */
public class AppAuthManager {

    public static final String TAG = AppAuthManager.class.getSimpleName();

    public void handleRequest(RequestType type, AuthModel data) {

        if (type == RequestType.REQ_AUTHORIZE && data.getAuthType() == AuthorizeType.AUTH_UNLOCK) {

            requestUnlock(data.getPassword());

        } else if (type == RequestType.REQ_AUTHORIZE) {

            requestAuthorization(data.getPassword(), data.getEmail());

        } else if (type == RequestType.REQ_REGISTER) {
            /*
                Entered credentials will be checked on the native side
                It will make a decision about the next step
            */
            requestRegistration(data.getPassword(), data.getConfirmPassword(), data.getEmail());

        } else if (type == RequestType.REQ_BIOMETRIC_LOGIN) {

            requestBiometricLogin();

        } else if (type == RequestType.REQ_UNLOCK_KEYSTORE) {

            requestUnlockKeystore();
        }

    }

    private native void requestAuthorization(String password, String username);
    private native void requestRegistration(String password, String confirmPassword, String username);
    private native void requestUnlock(String unlockKey);
    private native void requestBiometricLogin();
    private native void requestUnlockKeystore();

    static {
        System.loadLibrary(RUNTIME_LIBRARY);
    }

}
