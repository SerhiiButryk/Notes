package com.serhii.apps.notes.control;

import android.os.Handler;
import android.os.HandlerThread;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.serhii.core.log.Log;
import com.serhii.apps.notes.control.base.IAuthorizeService;
import com.serhii.apps.notes.control.base.IUnlockKeystore;
import com.serhii.apps.notes.control.managers.AppAuthManager;
import com.serhii.apps.notes.control.types.RequestType;
import com.serhii.apps.notes.ui.data_model.AuthModel;

/**
 *  Class which receives the events from the system and delivers them to corresponding manager
 *
 *  See interface definition
 */
public class EventService implements IAuthorizeService, IUnlockKeystore, LifecycleObserver {

    private static String TAG = EventService.class.getSimpleName();

    private static EventService instance;

    private HandlerThread serviceThread;
    private Handler handler;

    private boolean isUnlockKeystoreEventHandled = true;

    private EventService() {
        serviceThread = new HandlerThread("EventServiceThread");
        serviceThread.start();

        handler = new Handler(serviceThread.getLooper());
    }

    public static EventService getInstance() {
        if (instance == null) {
            instance = new EventService();
        }
        return instance;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void releaseResources() {
        if (serviceThread != null) {
            serviceThread.quit();
            serviceThread = null;
        }
    }

    /**
     *  Handle authorize event
     */
    @Override
    public void onBasicLogin(AuthModel model) {
        AppAuthManager appAuthManager = new AppAuthManager();
        // Initiate authorize request
        appAuthManager.handleRequest(RequestType.REQ_AUTHORIZE, model);
    }

    /**
     *  Handle registration event
     */
    @Override
    public void onRegistration(AuthModel model) {
        AppAuthManager appAuthManager = new AppAuthManager();
        // Initiate authorize request
        appAuthManager.handleRequest(RequestType.REQ_REGISTER, model);
    }

    /**
     *  Handle finger print login event
     */
    @Override
    public void onBiometricLogin() {
        AppAuthManager appAuthManager = new AppAuthManager();
        // Initiate authorize request
        appAuthManager.handleRequest(RequestType.REQ_BIOMETRIC_LOGIN, null);
    }

    /**
     *  Handle keystore unlock event
     */
    @Override
    public void onUnlockKeystore() {

        Log.info(TAG, "notifyOnUnlockKeystore()");

        if (isUnlockKeystoreEventHandled) {

            // Do not process a new event while current is not delivered
            isUnlockKeystoreEventHandled = false;

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    AppAuthManager appAuthManager = new AppAuthManager();
                    appAuthManager.handleRequest(RequestType.REQ_UNLOCK_KEYSTORE, null);
                }
            }, 1000);

        }

    }

    public void notifyUnlockEventReceived() {
        // Indicates that Unlock Dialog has shown
        isUnlockKeystoreEventHandled = true;
    }

}
