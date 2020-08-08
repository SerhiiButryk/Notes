package com.example.notes.test.control.data;

import com.example.notes.test.ui.data_model.AuthModel;

/**
 *  Global access point for app's data related to UIs
 */

public class DataProvider {

    private static DataProvider instance;

    /**
        Application light data objects

        Only used during application lifecycle
    */
    private AuthModel authorizationModel;

    private DataProvider() {
    }

    public static DataProvider getInstance() {
        if (instance == null) {
            instance = new DataProvider();
        }
        return instance;
    }

    public AuthModel getAuthorizeData() {
        return authorizationModel;
    }

    public void saveAuthorizeData(AuthModel authModel) { this.authorizationModel = authModel; }

}
