package com.example.notes.test;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.notes.test.control.NativeBridge;
import com.example.notes.test.control.logic.AuthResult;
import com.example.notes.test.control.logic.AuthorizeType;
import com.example.notes.test.control.logic.IAuthorize;
import com.example.notes.test.control.logic.IAuthorizeSubject;
import com.example.notes.test.control.EventService;
import com.example.notes.test.control.data.AppProvider;
import com.example.notes.test.ui.data_model.AuthModel;
import com.example.notes.test.ui.dialogs.impl.AlertDialog;
import com.example.notes.test.ui.fragments.BlockFragment;
import com.example.notes.test.ui.fragments.LoginFragment;
import com.example.notes.test.ui.fragments.RegisterFragment;
import com.example.notes.test.ui.view_model.AuthViewModel;
import com.example.core.common.log.Log;

import java.util.ArrayList;
import java.util.List;

import static com.example.notes.test.common.AppUtils.RUNTIME_LIBRARY;

public class AuthorizationActivity extends AppCompatActivity implements LoginFragment.LoginListener,
        IAuthorizeSubject, RegisterFragment.OnRegisterListener {

    private static final String TAG = "LoginActivity";
    private static final String LOGIN_FRAGMENT = "LoginFragment";

    private AuthViewModel authViewModel;
    private List<IAuthorize> observers = new ArrayList<>();
    private FragmentManager fragmentManager;
    private NativeBridge nativeBridge = new NativeBridge();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        fragmentManager = getSupportFragmentManager();

        showLoginFragment();

        authViewModel = ViewModelProviders.of(this).get(AuthViewModel.class);
        authViewModel.getValue().observe(this, new Observer<AuthModel>() {
            @Override
            public void onChanged(@Nullable AuthModel authModel) {
                // Save to local application data provider
                AppProvider.getInstance().saveAuthorizeData(authModel);
            }
        });

        subscribe(EventService.getInstance());
        initNative();

        Log.info(TAG, "onCreate() - fragments in the back stack " + fragmentManager.getBackStackEntryCount());
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.info(TAG, "onResume() - fragments in the back stack " + fragmentManager.getBackStackEntryCount());
    }

    @Override
    protected void onDestroy() {

        observers.clear();

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        Log.info(TAG, "onBackPressed() - fragments in the back stack " + fragmentManager.getBackStackEntryCount());

        if (fragmentManager.getBackStackEntryCount() == 1) {
            moveTaskToBack(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRegister() {
        // Open the Registration fragment view
        addFragment(new RegisterFragment(), null);
    }

    @Override
    public void onLoginRequested() { notifyObservers(AuthorizeType.AUTH_LOGIN_BASIC); }

    @Override
    public void onRegisterRequested() {
        notifyObservers(AuthorizeType.AUTH_REGISTRATION);
    }

    @Override
    public void onFingerPrintLoginRequested() { notifyObservers(AuthorizeType.AUTH_BIOMETRIC_LOGIN); }

    private void showLoginFragment() {
        if (fragmentManager.getBackStackEntryCount() == 0) {

            if (nativeBridge.isAppBlocked()) {

                BlockFragment blockFragment = new BlockFragment();

                addFragment(blockFragment, null);

            } else {

                LoginFragment loginFragment = LoginFragment.newInstance();

                addFragment(loginFragment, LOGIN_FRAGMENT);
            }

        }
    }

    private void addFragment(Fragment fragment, String tag) {
        fragmentManager.beginTransaction().replace(R.id.main_layout, fragment, tag)
                .addToBackStack(null)
                .commit();
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

    /**
     * Calls from native
     */
    public void onAuthorizationFinished() {
        // Close activity
        finish();

        Log.info(TAG, "onAuthorize() - activity finished");
    }

    /**
     * Calls from native
     */
    public void showRegistrationUI() {
        onBackPressed();

        LoginFragment loginFragment = (LoginFragment) fragmentManager.findFragmentByTag(LOGIN_FRAGMENT);
        loginFragment.onUserAccountCreated();

        Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show();
    }

    /**
     *  Calls from native
     */
    private void showAlertDialog(int type) {

        boolean isBlockNeeded = false;

        if (type == AuthResult.WRONG_PASSWORD.getTypeId()) {

            int currentLimit = nativeBridge.getLimitLeft();

            if (currentLimit == 1) {

                // Block application
                isBlockNeeded = true;

                Log.info(TAG, "showAlertDialog() - blocked due to password limit attempts ");

            } else {

                nativeBridge.setLimitLeft(currentLimit - 1);
            }

        }

        if (isBlockNeeded) {

            nativeBridge.executeBlockApp();

            clearFragmentStack();

            addFragment(new BlockFragment(), null);

        } else {

            AlertDialog.showDialog(this, type);
        }

        Log.info(TAG, "showAlertDialog() - type " + type);
    }

    private void clearFragmentStack() {

        int count = fragmentManager.getBackStackEntryCount();

        for (int i = 0; i < count; i++) {
            fragmentManager.popBackStack();
        }

    }

    /**
     *  Native interface
     */
    private native void initNative();

    static {
        System.loadLibrary(RUNTIME_LIBRARY);
    }

}