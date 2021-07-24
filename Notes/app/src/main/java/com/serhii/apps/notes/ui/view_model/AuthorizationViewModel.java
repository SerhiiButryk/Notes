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

public class AuthorizationViewModel extends ViewModel {

    private IAuthorizeService authorizeService = EventService.getInstance();

    private MutableLiveData<AuthModel> authModel = new MutableLiveData<>();

    public void setAuthValue(AuthModel auth) {
        authModel.setValue(auth);
    }

    public LiveData<AuthModel> getAuthValue() {
        return authModel;
    }

    public IAuthorizeService getAuthorizeService() { return authorizeService; }
}
