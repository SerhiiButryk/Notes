package com.example.notes.test.control.data;

import com.example.notes.test.ui.data_model.AuthModel;

/**
 *  Global access point for app's data related to UIs
 */

public class AppProvider {

    private static AppProvider instance;

    /**
        Application light data objects

        Only used during application general life cycle
    */
    private AuthModel authorizationModel;

    private AppProvider() {
    }

    public static AppProvider getInstance() {
        if (instance == null) {
            instance = new AppProvider();
        }
        return instance;
    }

    public AuthModel getAuthorizeData() {
        return authorizationModel;
    }

    public void saveAuthorizeData(AuthModel authModel) { this.authorizationModel = authModel; }

}
