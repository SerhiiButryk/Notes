package com.example.notes.test.control.base;

import com.example.notes.test.ui.data_model.AuthModel;

/**
 *   The interface to handle user authorization events
 */

public interface IAuthorizeService {

    void onBasicLogin(AuthModel model);

    void onRegistration(AuthModel model);

    void onBiometricLogin();

}
