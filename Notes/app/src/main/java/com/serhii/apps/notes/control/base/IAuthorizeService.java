package com.serhii.apps.notes.control.base;

import android.content.Context;

import com.serhii.apps.notes.ui.data_model.AuthModel;

/**
 *   The interface to handle user authorization events
 */

public interface IAuthorizeService {

    void onPasswordLogin(AuthModel model);

    void onRegistration(AuthModel model);

    void onBiometricLogin();

    void onUserRegistered(Context context);

}
