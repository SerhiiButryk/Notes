package com.serhii.apps.notes.activities;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.serhii.apps.notes.R;
import com.serhii.apps.notes.control.EventService;
import com.serhii.apps.notes.control.NativeBridge;
import com.serhii.apps.notes.control.types.AuthResult;
import com.serhii.apps.notes.control.types.AuthorizeType;
import com.serhii.apps.notes.ui.data_model.AuthModel;
import com.serhii.apps.notes.ui.dialogs.DialogHelper;
import com.serhii.apps.notes.ui.fragments.BlockFragment;
import com.serhii.apps.notes.ui.fragments.LoginFragment;
import com.serhii.apps.notes.ui.fragments.RegisterFragment;
import com.serhii.apps.notes.ui.view_model.AuthorizationViewModel;
import com.serhii.core.log.Log;

import static com.serhii.apps.notes.common.AppConstants.RUNTIME_LIBRARY;

public class AuthorizationActivity extends AppCompatActivity implements LoginFragment.ShowRegistrationUIListener {

    private static final String TAG = AuthorizationActivity.class.getSimpleName();

    private AuthorizationViewModel authorizationViewModel;
    private FragmentManager fragmentManager;
    private NativeBridge nativeBridge = new NativeBridge();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        // Enable unsecured screen content settings
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        fragmentManager = getSupportFragmentManager();

        showLoginFragment(savedInstanceState);

        authorizationViewModel = new ViewModelProvider(this).get(AuthorizationViewModel.class);

        setupObservers();
        initNative();

        Log.info(TAG, "onCreate() - fragments in the back stack " + fragmentManager.getBackStackEntryCount());
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.info(TAG, "onResume() - fragments in the back stack " + fragmentManager.getBackStackEntryCount());
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
    public void onShowRegistrationUI() {
        addFragment(new RegisterFragment(), null);
    }

    private void showLoginFragment(Bundle savedInstanceState) {

        // Ensure that the fragment is added only once when the activity
        // is launched the first time. When configuration is changed the fragment
        // doesn't need to be added as it is restored from the savedInstanceState
        if (savedInstanceState == null) {

            if (nativeBridge.isAppBlocked()) {

                BlockFragment blockFragment = new BlockFragment();

                addFragment(blockFragment, null);

            } else {

                LoginFragment loginFragment = LoginFragment.newInstance();

                addFragment(loginFragment, LoginFragment.FRAGMENT_TAG);
            }

        }
    }

    private void setupObservers() {
        authorizationViewModel.getAuthValue().observe(this, new Observer<AuthModel>() {
            @Override
            public void onChanged(@Nullable AuthModel authModel) {
                AuthorizeType type = authModel.getAuthType();
                switch (type) {
                    case AUTH_UNLOCK:
                    case AUTH_BASIC_LOGIN:
                        EventService.getInstance().onBasicLogin(authModel);
                        break;
                    case AUTH_REGISTRATION:
                        EventService.getInstance().onRegistration(authModel);
                        break;
                    case AUTH_BIOMETRIC_LOGIN:
                        EventService.getInstance().onBiometricLogin();
                        break;
                }
            }
        });
    }

    private void addFragment(Fragment fragment, String tag) {
        fragmentManager.beginTransaction().replace(R.id.main_layout, fragment, tag)
                .setReorderingAllowed(true) // Needed for optimization
                .addToBackStack(null)
                .commit();
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
    public void showRegistrationUI() {
        Log.info(TAG, "showRegistrationUI()");

        onBackPressed();

        LoginFragment loginFragment = (LoginFragment) fragmentManager.findFragmentByTag(LoginFragment.FRAGMENT_TAG);
        loginFragment.onUserAccountCreated();

        Toast.makeText(this, getString(R.string.toast_registration_done), Toast.LENGTH_SHORT).show();
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

            addFragment(new BlockFragment(), null);

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