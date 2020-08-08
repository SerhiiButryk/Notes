package com.example.notes.test.control;

import android.os.Handler;
import android.os.HandlerThread;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.example.core.common.log.Log;
import com.example.notes.test.control.data.DataProvider;
import com.example.notes.test.control.logic.IAuthorize;
import com.example.notes.test.control.logic.IUnlockKeystore;
import com.example.notes.test.control.managers.AuthorizeManager;
import com.example.notes.test.control.logic.RequestType;
import com.example.notes.test.ui.data_model.AuthModel;

/**
 *  Class which receives the events from the system and delivers it to corresponding manager
 *
 *  Could receive update commands from any subscribed subjects
 *
 *  See interface definition
 */
public class EventService implements IAuthorize, IUnlockKeystore, LifecycleObserver {

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
        serviceThread.quit();
        serviceThread = null;
    }

    /**
     *  Handle authorize event
     */
    @Override
    public void onAuthorization() {
        // Retrieve authorization data model
        AuthModel authModel = DataProvider.getInstance().getAuthorizeData();

        AuthorizeManager authorizeManager = new AuthorizeManager();

        // Initiate authorize request
        authorizeManager.handleRequest(RequestType.REQ_AUTHORIZE, authModel);
    }

    /**
     *  Handle registration event
     */
    @Override
    public void onRegistration() {
        // Retrieve authorization data model
        AuthModel authModel = DataProvider.getInstance().getAuthorizeData();

        AuthorizeManager authorizeManager = new AuthorizeManager();

        // Initiate authorize request
        authorizeManager.handleRequest(RequestType.REQ_REGISTER, authModel);
    }

    @Override
    public void onBiometricLogin() {
        AuthorizeManager authorizeManager = new AuthorizeManager();

        authorizeManager.handleRequest(RequestType.REQ_BIOMETRIC_LOGIN, null);
    }

    @Override
    public void onUnlockKeystore() {

        Log.info(TAG, "onUnlockKeystore()");

        if (isUnlockKeystoreEventHandled) {

            // Do not process a new event while current is not delivered
            isUnlockKeystoreEventHandled = false;

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    AuthorizeManager authorizeManager = new AuthorizeManager();

                    authorizeManager.handleRequest(RequestType.REQ_UNLOCK_KEYSTORE, null);
                }
            }, 1000);

        }

    }

    public void notifyEventReceived() {
        // Indicates that Unlock Dialog has shown
        isUnlockKeystoreEventHandled = true;
    }

}
