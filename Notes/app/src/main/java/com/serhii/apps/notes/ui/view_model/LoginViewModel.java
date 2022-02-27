package com.serhii.apps.notes.ui.view_model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.serhii.apps.notes.control.EventService;
import com.serhii.apps.notes.control.base.IAuthorizeService;
import com.serhii.apps.notes.ui.data_model.AuthModel;

/**
 *  View model for managing authentication data and User's actions.
 */

public class LoginViewModel extends ViewModel {

    private final IAuthorizeService authorizeService = EventService.getInstance();
    private final MutableLiveData<Boolean> showRegistrationUISetFlag = new MutableLiveData<>(false);

    public void setAuthValue(AuthModel newValue) {
        // If data is set then perform corresponding action
        if (newValue != null) {
            switch (newValue.getAuthType()) {
                case AUTH_UNLOCK:
                case AUTH_PASSWORD_LOGIN:
                    // Proceed with Login
                    authorizeService.onPasswordLogin(newValue);
                    break;
                case AUTH_REGISTRATION:
                    // Proceed with User's registration
                    authorizeService.onRegistration(newValue);
                    break;
                case AUTH_BIOMETRIC_LOGIN:
                    // Proceed with Login
                    authorizeService.onBiometricLogin();
                    break;
            }
        }
    }

    public IAuthorizeService getAuthorizeService() { return authorizeService; }

    public void showRegistrationUI() {
        // Notify observers that action should be performed
        showRegistrationUISetFlag.setValue(true);
        // Reset flag to notify that activity doesn't need to perform any action
        showRegistrationUISetFlag.setValue(false);
    }

    public LiveData<Boolean> getShowRegistrationUISetFlag() {
        return showRegistrationUISetFlag;
    }

}
