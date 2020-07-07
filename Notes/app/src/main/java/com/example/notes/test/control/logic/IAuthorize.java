package com.example.notes.test.control.logic;

/**
 *   The interface receives a user authorization event
 *
 *   All interested parts which implements this interface and are registered to corresponding observer
 *
 *   will receive a user authorization event
 */

public interface IAuthorize {

    void onAuthorization();

    void onRegistration();

    void onBiometricLogin();

}
