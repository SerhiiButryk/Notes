package com.serhii.apps.notes.control.base;

import com.serhii.apps.notes.ui.data_model.AuthModel;

/**
 *   The interface to handle user authorization events
 */

public interface IAuthorizeService {

    void onBasicLogin(AuthModel model);

    void onRegistration(AuthModel model);

    void onBiometricLogin();

}
