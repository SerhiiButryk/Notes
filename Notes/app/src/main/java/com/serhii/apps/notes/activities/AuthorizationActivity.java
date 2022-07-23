package com.serhii.apps.notes.activities;

import static com.serhii.apps.notes.common.AppConstants.RUNTIME_LIBRARY;
import static com.serhii.apps.notes.ui.fragments.RegisterFragment.FRAGMENT_TAG;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.serhii.apps.notes.R;
import com.serhii.apps.notes.control.NativeBridge;
import com.serhii.apps.notes.control.base.IAuthorizeService;
import com.serhii.apps.notes.control.managers.InactivityManager;
import com.serhii.apps.notes.control.types.AuthResult;
import com.serhii.apps.notes.ui.dialogs.DialogHelper;
import com.serhii.apps.notes.ui.fragments.BlockFragment;
import com.serhii.apps.notes.ui.fragments.LoginFragment;
import com.serhii.apps.notes.ui.fragments.RegisterFragment;
import com.serhii.apps.notes.ui.view_model.LoginViewModel;
import com.serhii.core.log.Log;
import com.serhii.core.utils.GoodUtils;

public class AuthorizationActivity extends AppCompatActivity {

    private static final String TAG = "AuthorizationActivity";

    private LoginViewModel loginViewModel;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

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
        // Cancel inactivity timer
        InactivityManager.getInstance().cancelAlarm();
        Log.info(TAG, "onResume()");
    }

    @Override
    public void onBackPressed() {
        Log.info(TAG, "onBackPressed()");

        // Allow user to go back to the Login UI
        // if there is one fragment in the stack which means that Registration UI is displayed
        if (fragmentManager.getBackStackEntryCount() == 1) {
            super.onBackPressed();
        } else {
            moveTaskToBack(true);
        }

    }

    private void showLoginFragment(Bundle savedInstanceState) {

        // Ensure that the fragment is added only once when the activity
        // is launched the first time. When configuration is changed the fragment
        // doesn't need to be added as it is restored from the 'savedInstanceState' Bundle
        if (savedInstanceState == null) {

            NativeBridge nativeBridge = new NativeBridge();

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

        loginViewModel.getShowRegistrationUISetFlag().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean shouldPerformAction) {
                // Do action if it is needed
                if (shouldPerformAction) {
                    Log.info(TAG, "addFragment(), open Registration fragment");
                    addFragment(new RegisterFragment(), FRAGMENT_TAG, true);
                }
            }
        });
    }

    private void addFragment(Fragment fragment, String tag, boolean shouldSave) {

        if (tag != null && fragmentManager.findFragmentByTag(tag) != null) {
            Log.info(TAG, "addFragment(), fragment is already opened");
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

        Log.info(TAG, "onAuthorize(), activity finished");
    }

    /**
     * Called from native
     */
    public void userRegistered() {
        Log.info(TAG, "userRegistered()");

        // Close Registration UI
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
        Log.info(TAG, "showAlertDialog(), type " + type);

        boolean shouldShowDialog = false;

        if (type == AuthResult.WRONG_PASSWORD.getTypeId()) {

            NativeBridge nativeBridge = new NativeBridge();

            int currentLimit = nativeBridge.getLimitLeft();

            // If limit is exceeded then need to block application
            if (currentLimit == 1) {

                // Block application
                nativeBridge.executeBlockApp();

                clearFragmentStack();

                addFragment(new BlockFragment(), null, false);

                shouldShowDialog = true;

                Log.info(TAG, "showAlertDialog(), BB SS");

            } else {
                // Update password limit value
                nativeBridge.setLimitLeft(currentLimit - 1);

                Log.info(TAG, "showAlertDialog(), AA SS");
            }

        }

        if (shouldShowDialog) {
            Log.info(TAG, "showAlertDialog(), show dialog");
            DialogHelper.showAlertDialog(type, this);
        }

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