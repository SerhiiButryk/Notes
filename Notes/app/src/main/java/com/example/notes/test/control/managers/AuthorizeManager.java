package com.example.notes.test.control.managers;

import com.example.notes.test.control.logic.AuthorizeType;
import com.example.notes.test.ui.data_model.AuthModel;
import com.example.notes.test.control.logic.UserRequest;

import static com.example.notes.test.common.AppUtils.RUNTIME_LIBRARY;

/**
 *  Performs user authorization request processing
 */
public class AuthorizeManager {

    public static final String TAG = "AuthorizeManager";

    public AuthorizeManager() {
    }

    public void handleRequest(UserRequest type, AuthModel requestData) {

        if (type == UserRequest.REQ_AUTHORIZE && requestData.getAuthType() == AuthorizeType.AUTH_UNLOCK) {

            requestUnlock(requestData.getPassword());

        } else if (type == UserRequest.REQ_AUTHORIZE) {

            requestAuthorization(requestData.getPassword(), requestData.getEmail());

        } else if (type == UserRequest.REQ_REGISTER) {
            /*
                Entered credentials will be checked on the native side
                It will make a decision about the next step
            */
            requestRegistration(requestData.getPassword(), requestData.getConfirmPassword(), requestData.getEmail());

        } else if (type == UserRequest.REQ_BIOMETRIC_LOGIN) {

            requestBiometricLogin();
        }

    }

    private native void requestAuthorization(String password, String username);
    private native void requestRegistration(String password, String confirmPassword, String username);
    private native void requestUnlock(String unlockKey);
    private native void requestBiometricLogin();

    static {
        System.loadLibrary(RUNTIME_LIBRARY);
    }

}
