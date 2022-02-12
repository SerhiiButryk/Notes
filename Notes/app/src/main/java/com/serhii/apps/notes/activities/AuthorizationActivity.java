package com.serhii.apps.notes.activities;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.serhii.apps.notes.R;
import com.serhii.apps.notes.control.NativeBridge;
import com.serhii.apps.notes.control.base.IAuthorizeService;
import com.serhii.apps.notes.control.types.AuthResult;
import com.serhii.apps.notes.control.types.AuthorizeType;
import com.serhii.apps.notes.ui.data_model.AuthModel;
import com.serhii.apps.notes.ui.dialogs.DialogHelper;
import com.serhii.apps.notes.ui.fragments.BlockFragment;
import com.serhii.apps.notes.ui.fragments.LoginFragment;
import com.serhii.apps.notes.ui.fragments.RegisterFragment;
import com.serhii.apps.notes.ui.view_model.LoginViewModel;
import com.serhii.core.log.Log;
import com.serhii.core.utils.GoodUtils;

import static com.serhii.apps.notes.common.AppConstants.RUNTIME_LIBRARY;
import static com.serhii.apps.notes.ui.fragments.RegisterFragment.FRAGMENT_TAG;

public class AuthorizationActivity extends AppCompatActivity {

    private static final String TAG = AuthorizationActivity.class.getSimpleName();

    private LoginViewModel loginViewModel;
    private FragmentManager fragmentManager;
    private final NativeBridge nativeBridge = new NativeBridge();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        // Enable unsecured screen content settings
        boolean isUnsecureScreenEnabled = GoodUtils.enableUnsecureScreenProtection(this);
        Log.info(TAG, "onCreate() SS: " + isUnsecureScreenEnabled);

        fragmentManager = getSupportFragmentManager();

        showLoginFragment(savedInstanceState);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        setupObservers();
        initNative();

        Log.info(TAG, "onCreate() activity created");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.info(TAG, "onResume()");
    }

    @Override
    public void onBackPressed() {
        Log.info(TAG, "onBackPressed()");

        // Allow user to go back to the Login UI
        // If there is one fragment in the stack then Registration UI is displayed
        if (fragmentManager.getBackStackEntryCount() == 1) {
            super.onBackPressed();
        } else {
            moveTaskToBack(true);
        }

    }

    private void showLoginFragment(Bundle savedInstanceState) {

        // Ensure that the fragment is added only once when the activity
        // is launched the first time. When configuration is changed the fragment
        // doesn't need to be added as it is restored from the savedInstanceState
        if (savedInstanceState == null) {

            if (nativeBridge.isAppBlocked()) {

                BlockFragment blockFragment = new BlockFragment();

                addFragment(blockFragment, null, false);

            } else {

                LoginFragment loginFragment = LoginFragment.newInstance();

                addFragment(loginFragment, LoginFragment.FRAGMENT_TAG, false);
            }

        }
    }

    private void setupObservers() {
        loginViewModel.getAuthModelSetFlag().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean value) {
                AuthModel authModel = loginViewModel.getAuthValue();
                IAuthorizeService authorizeService = loginViewModel.getAuthorizeService();
                // We react only if value is true
                if (value && authModel != null) {
                    switch (authModel.getAuthType()) {
                        case AUTH_UNLOCK:
                        case AUTH_PASSWORD_LOGIN:
                            authorizeService.onPasswordLogin(authModel);
                            break;
                        case AUTH_REGISTRATION:
                            authorizeService.onRegistration(authModel);
                            break;
                        case AUTH_BIOMETRIC_LOGIN:
                            authorizeService.onBiometricLogin();
                            break;
                    }
                }
            }
        });

        loginViewModel.getShowRegistrationUISetFlag().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                // We react only if value is true
                if (aBoolean) {
                    Log.info(TAG, "addFragment() - open Registration fragment");
                    addFragment(new RegisterFragment(), FRAGMENT_TAG, true);
                }
            }
        });
    }

    private void addFragment(Fragment fragment, String tag, boolean shouldSave) {

        if (tag != null && fragmentManager.findFragmentByTag(tag) != null) {
            Log.info(TAG, "addFragment() - fragment is already opened");
            return;
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.main_layout, fragment, tag);
        transaction.setReorderingAllowed(true); // Needed for optimization

        if (shouldSave) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    /**
     * Called from native
     */
    public void onAuthorizationFinished() {
        // Close activity
        finish();

        Log.info(TAG, "onAuthorize() - activity finished");
    }

    /**
     * Called from native
     */
    public void userRegistered() {
        Log.info(TAG, "userRegistered()");

        onBackPressed();

        LoginFragment loginFragment = (LoginFragment) fragmentManager.findFragmentByTag(LoginFragment.FRAGMENT_TAG);
        loginFragment.onUserAccountCreated();

        IAuthorizeService authorizeService = loginViewModel.getAuthorizeService();
        authorizeService.onUserRegistered(this);
    }

    /**
     *  Called from native
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

            addFragment(new BlockFragment(), null, false);

        } else {

            DialogHelper.showAlertDialog(type, this);
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