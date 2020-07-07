package com.example.notes.test.control;

import com.example.notes.test.control.data.AppProvider;
import com.example.notes.test.control.logic.IAuthorize;
import com.example.notes.test.control.managers.AuthorizeManager;
import com.example.notes.test.control.logic.UserRequest;
import com.example.notes.test.ui.data_model.AuthModel;

/**
 *  Class which receives the events from the system and delivers it to corresponding manager
 *
 *  Could receive update commands from any subscribed services
 *
 *  See the implemented interface definition
 */
public class EventService implements IAuthorize {

    private static EventService instance;

    private EventService() {
    }

    public static EventService getInstance() {
        if (instance == null) {
            instance = new EventService();
        }
        return instance;
    }

    /**
     *  Handle authorize event
     */
    @Override
    public void onAuthorization() {
        // Retrieve authorization data model
        AuthModel authModel = AppProvider.getInstance().getAuthorizeData();

        AuthorizeManager authorizeManager = new AuthorizeManager();

        // Initiate authorize request
        authorizeManager.handleRequest(UserRequest.REQ_AUTHORIZE, authModel);
    }

    /**
     *  Handle registration event
     */
    @Override
    public void onRegistration() {
        // Retrieve authorization data model
        AuthModel authModel = AppProvider.getInstance().getAuthorizeData();

        AuthorizeManager authorizeManager = new AuthorizeManager();

        // Initiate authorize request
        authorizeManager.handleRequest(UserRequest.REQ_REGISTER, authModel);
    }

    @Override
    public void onBiometricLogin() {
        AuthorizeManager authorizeManager = new AuthorizeManager();

        authorizeManager.handleRequest(UserRequest.REQ_BIOMETRIC_LOGIN, null);
    }
}
