package com.example.notes.test.ui.view_model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.notes.test.ui.data_model.AuthModel;

public class ChangePasswordViewModel extends ViewModel {

    private MutableLiveData<AuthModel> authModel = new MutableLiveData<>();

    public LiveData<AuthModel> getValue() {
        return authModel;
    }

    public void setValue(AuthModel authModel) {
        this.authModel.setValue(authModel);
    }
}
