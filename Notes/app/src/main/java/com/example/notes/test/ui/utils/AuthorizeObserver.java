package com.example.notes.test.ui.utils;

import com.example.notes.test.control.EventService;
import com.example.notes.test.control.logic.AuthorizeType;
import com.example.notes.test.control.logic.IAuthorize;
import com.example.notes.test.control.logic.IAuthorizeSubject;
import com.example.notes.test.ui.fragments.BlockFragment;
import com.example.notes.test.ui.fragments.LoginFragment;
import com.example.notes.test.ui.fragments.RegisterFragment;

import java.util.HashSet;
import java.util.Set;

/**
 *  Handle communication between AuthorizationActivity and its fragments
 */

public class AuthorizeObserver implements LoginFragment.LoginListener,
        IAuthorizeSubject, RegisterFragment.OnRegisterListener, BlockFragment.UnlockApplicationListener {

    public interface RegisterNewAccountListener {
        void onRegisterNewAccountClicked();
    }

    private Set<IAuthorize> observers = new HashSet<>();
    private RegisterNewAccountListener registerNewAccountListener;

    public AuthorizeObserver(RegisterNewAccountListener registerNewAccountListener) {
        // Subscribe interested parties
        subscribe(EventService.getInstance());

        this.registerNewAccountListener = registerNewAccountListener;
    }

    public void clear() {
        observers.clear();
        registerNewAccountListener = null;
    }

    @Override
    public void subscribe(IAuthorize observer) {
        observers.add(observer);
    }

    @Override
    public void unsubscribe(IAuthorize observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(AuthorizeType type) {
        for (IAuthorize observer : observers) {

            if (type == AuthorizeType.AUTH_REGISTRATION) {

                observer.onRegistration();

            } else if (type == AuthorizeType.AUTH_LOGIN_BASIC) {

                observer.onAuthorization();

            } else if (type == AuthorizeType.AUTH_BIOMETRIC_LOGIN) {

                observer.onBiometricLogin();
            }

        }
    }

    @Override
    public void onRegisterNewAccountClicked() {
        if (registerNewAccountListener != null) {
            registerNewAccountListener.onRegisterNewAccountClicked();
        }
    }

    @Override
    public void onLoginClicked() {
        notifyObservers(AuthorizeType.AUTH_LOGIN_BASIC);
    }

    @Override
    public void onFingerPrintLoginClicked() {
        notifyObservers(AuthorizeType.AUTH_BIOMETRIC_LOGIN);
    }

    @Override
    public void onRegisterClicked() {
        notifyObservers(AuthorizeType.AUTH_REGISTRATION);
    }

    @Override
    public void onApplicationUnlock() {

    }
}
