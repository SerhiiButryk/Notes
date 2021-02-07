package com.serhii.apps.notes.ui.view_model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.serhii.apps.notes.ui.data_model.AuthModel;

public class AuthorizationViewModel extends ViewModel {

    private MutableLiveData<AuthModel> authModel = new MutableLiveData<>();

    public void setAuthValue(AuthModel auth) {
        authModel.setValue(auth);
    }

    public LiveData<AuthModel> getAuthValue() {
        return authModel;
    }
}
