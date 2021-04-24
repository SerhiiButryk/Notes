package com.serhii.apps.notes.control;

import android.content.Context;

import com.serhii.apps.notes.common.AppConstants;
import com.serhii.apps.notes.control.base.IAuthorizeService;
import com.serhii.apps.notes.control.managers.AuthManager;
import com.serhii.apps.notes.control.types.RequestType;
import com.serhii.apps.notes.ui.data_model.AuthModel;
import com.serhii.core.log.Log;
import com.serhii.core.security.Cipher;

/**
 *  Class which receives the events from the system and delivers them to corresponding manager
 *
 *  See interface definition
 */
public class EventService implements IAuthorizeService {

    private static String TAG = EventService.class.getSimpleName();

    private static EventService instance;

    public static EventService getInstance() {
        if (instance == null) {
            synchronized (EventService.class) {
                if (instance == null) {
                    instance = new EventService();
                }
            }
        }
        return instance;
    }

    /**
     *  Handle authorize event
     */
    @Override
    public void onPasswordLogin(AuthModel model) {
        AuthManager authManager = new AuthManager();
        // Initiate authorize request
        authManager.handleRequest(RequestType.REQ_AUTHORIZE, model);
    }

    /**
     *  Handle registration event
     */
    @Override
    public void onRegistration(AuthModel model) {
        AuthManager authManager = new AuthManager();
        // Initiate authorize request
        authManager.handleRequest(RequestType.REQ_REGISTER, model);
    }

    /**
     *  Handle finger print login event
     */
    @Override
    public void onBiometricLogin() {
        AuthManager authManager = new AuthManager();
        // Initiate authorize request
        authManager.handleRequest(RequestType.REQ_BIOMETRIC_LOGIN, null);
    }

    public void onUserRegistered(Context context) {
        Log.info(TAG, "onUserRegistered()");

        // Create protection keys
        Cipher cipher = new Cipher();
        cipher.createKey(AppConstants.SECRET_KEY_DATA_ENC_ALIAS, true);
        cipher.createKey(AppConstants.SECRET_KEY_PASSWORD_ENC_ALIAS, false);

        // Set login password limit
        new NativeBridge().setLoginLimitFromDefault(context);
    }

}
