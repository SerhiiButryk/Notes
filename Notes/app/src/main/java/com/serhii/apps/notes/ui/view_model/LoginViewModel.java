package com.serhii.apps.notes.ui.view_model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.serhii.apps.notes.control.EventService;
import com.serhii.apps.notes.control.base.IAuthorizeService;
import com.serhii.apps.notes.ui.data_model.AuthModel;

/**
 *  View model for AuthorizationActivity activity
 */

public class LoginViewModel extends ViewModel {

    private final IAuthorizeService authorizeService = EventService.getInstance();
    private final MutableLiveData<Boolean> authModelSetFlag = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> showRegistrationUISetFlag = new MutableLiveData<>(false);
    private AuthModel authModel;

    public void setAuthValue(AuthModel newValue) {
        authModel = newValue;
        // Notify observers that value is set
        authModelSetFlag.setValue(true);
        // Reset flag
        authModelSetFlag.setValue(false);
    }

    public AuthModel getAuthValue() {
        return authModel;
    }

    public IAuthorizeService getAuthorizeService() { return authorizeService; }

    public LiveData<Boolean> getAuthModelSetFlag() {
        return authModelSetFlag;
    }

    public void showRegistrationUI() {
        // Notify observers that value is set
        showRegistrationUISetFlag.setValue(true);
        // Reset flag
        showRegistrationUISetFlag.setValue(false);
    }

    public LiveData<Boolean> getShowRegistrationUISetFlag() {
        return showRegistrationUISetFlag;
    }

}
