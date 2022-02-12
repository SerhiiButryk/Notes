package com.serhii.apps.notes.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.serhii.apps.notes.R;
import com.serhii.apps.notes.control.NativeBridge;
import com.serhii.apps.notes.control.managers.BiometricAuthManager;
import com.serhii.apps.notes.control.types.AuthorizeType;
import com.serhii.apps.notes.databinding.FragmentLoginViewBinding;
import com.serhii.apps.notes.ui.data_model.AuthModel;
import com.serhii.apps.notes.ui.fragments.base.IViewBindings;
import com.serhii.apps.notes.ui.view_model.LoginViewModel;
import com.serhii.core.log.Log;
import com.serhii.core.security.Hash;
import com.serhii.core.utils.GoodUtils;

public class LoginFragment extends Fragment implements IViewBindings {

    private static final String TAG = LoginFragment.class.getSimpleName();
    public static final String FRAGMENT_TAG = "LoginFragment";

    private LoginViewModel loginViewModel;

    private EditText emailField;
    private EditText passwordField;
    private Button loginButton;
    private Button registerAccountBtn;
    private TextView titleLabel;
    private Button fingerprintBtn;
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;

    private final BiometricAuthManager biometricAuthManager = new BiometricAuthManager();
    private boolean isFingerprintAvailable;
    private final NativeBridge nativeBridge = new NativeBridge();

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    private final EditText.OnEditorActionListener keyEventActionDone = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event)  {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (GoodUtils.getText(passwordField).isEmpty() || GoodUtils.getText(emailField).isEmpty()) {
                    Toast.makeText(getContext(), getString(R.string.empty_login), Toast.LENGTH_LONG).show();
                    return true;
                }

                // Set data
                loginViewModel.setAuthValue(createModel(AuthorizeType.AUTH_PASSWORD_LOGIN));
                return true;
            }
            return false;
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (biometricAuthManager.canAuthenticate(context) && biometricAuthManager.hasFingerPrint(context))
        {
            biometricAuthManager.initBiometricSettings(context, this);
            isFingerprintAvailable = true;
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Retrieve an instance of ViewModel
        loginViewModel = new ViewModelProvider(getActivity()).get(LoginViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.info(TAG, "onCreateView()");

        View view = initBinding(inflater, container);

        String userName = nativeBridge.getUserName();

        // If user name is not empty then user is already registered.
        // Otherwise, we should ask for registration.
        if (!userName.isEmpty()) {

            Log.info(TAG, "onCreateView() user exists");

            emailField.setText(userName);
            registerAccountBtn.setVisibility(View.GONE);
            passwordField.requestFocus();

            loginButton.setVisibility(View.VISIBLE);
            passwordLayout.setVisibility(View.VISIBLE);
            emailLayout.setVisibility(View.VISIBLE);

            if (isFingerprintAvailable) {
                fingerprintBtn.setVisibility(View.VISIBLE);
            } else {
                fingerprintBtn.setVisibility(View.GONE);
            }

        } else {
            Log.info(TAG, "onCreateView() user doesn't exist");

            registerAccountBtn.setVisibility(View.VISIBLE);
            passwordLayout.setVisibility(View.GONE);
            emailLayout.setVisibility(View.GONE);
        }

        fingerprintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                biometricAuthManager.authenticate();
            }
        });

        biometricAuthManager.setOnAuthenticateSuccess(new BiometricAuthManager.OnAuthenticateListener() {
            @Override
            public void onSuccess() {
                // Set data
                loginViewModel.setAuthValue(createEmptyModel(AuthorizeType.AUTH_BIOMETRIC_LOGIN));
            }
        });

        titleLabel.setText(getString(R.string.title_login));

        registerAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginViewModel.showRegistrationUI();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GoodUtils.getText(passwordField).isEmpty() || GoodUtils.getText(emailField).isEmpty()) {
                    Toast.makeText(getContext(), getString(R.string.empty_login), Toast.LENGTH_LONG).show();
                    return;
                }

                // Set data
                loginViewModel.setAuthValue(createModel(AuthorizeType.AUTH_PASSWORD_LOGIN));
            }
        });

        passwordField.setOnEditorActionListener(keyEventActionDone);

        return view;
    }

    public void onUserAccountCreated() {
        registerAccountBtn.setVisibility(View.GONE);
        emailField.setText(nativeBridge.getUserName());
        passwordField.requestFocus();
    }

    @Override
    public View initBinding(LayoutInflater inflater, ViewGroup viewGroup) {
        FragmentLoginViewBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login_view, viewGroup, false);

        // Set references
        emailField = binding.inputEmail;
        passwordField = binding.inputPassword;
        loginButton = binding.btnLogin;
        registerAccountBtn = binding.btnRegister;
        titleLabel = binding.title;
        fingerprintBtn = binding.btnLoginBiometric;

        emailLayout = binding.emailLayout;
        passwordLayout = binding.passwordLayout;

        return binding.getRoot();
    }

    private AuthModel createModel(AuthorizeType type) {

        Hash hash = new Hash();

        AuthModel authModel = new AuthModel();

        authModel.setPassword(hash.hashMD5(GoodUtils.getText(passwordField)));
        authModel.setEmail(GoodUtils.getText(emailField));
        authModel.setAuthType(type);

        return authModel;
    }

    private AuthModel createEmptyModel(AuthorizeType type) {
        AuthModel authModel = new AuthModel();
        authModel.setAuthType(type);
        return authModel;
    }

}
