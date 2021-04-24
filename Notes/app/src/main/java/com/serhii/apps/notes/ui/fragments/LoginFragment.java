package com.serhii.apps.notes.ui.fragments;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.serhii.core.security.Hash;
import com.serhii.apps.notes.R;
import com.serhii.apps.notes.control.NativeBridge;
import com.serhii.apps.notes.control.types.AuthorizeType;
import com.serhii.apps.notes.control.managers.BiometricAuthManager;
import com.serhii.apps.notes.databinding.FragmentLoginViewBinding;
import com.serhii.apps.notes.ui.data_model.AuthModel;
import com.serhii.apps.notes.ui.fragments.base.IViewBindings;
import com.serhii.apps.notes.ui.view_model.AuthorizationViewModel;
import com.serhii.core.utils.GoodUtils;

public class LoginFragment extends Fragment implements IViewBindings {

    private static String TAG = LoginFragment.class.getSimpleName();
    public static final String FRAGMENT_TAG = "LoginFragment";

    private AuthorizationViewModel authorizationViewModel;

    private EditText emailField;
    private EditText passwordField;
    private Button loginButton;
    private TextView registerAccountBtn;
    private TextView titleLabel;
    private ImageButton fingerprintBtn;

    private BiometricAuthManager biometricAuthManager = new BiometricAuthManager();
    private boolean isFingerprintAvailable;
    private final NativeBridge nativeBridge = new NativeBridge();

    private ShowRegistrationUIListener showRegistrationUIListener;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    private EditText.OnEditorActionListener keyEventActionDone = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event)  {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Set data
                authorizationViewModel.setAuthValue(createModel(AuthorizeType.AUTH_PASSWORD_LOGIN));
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
        authorizationViewModel = new ViewModelProvider(getActivity()).get(AuthorizationViewModel.class);

        showRegistrationUIListener = (ShowRegistrationUIListener) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = initBinding(inflater, container);

        String userName = nativeBridge.getUserName();

        if (!userName.isEmpty()) {

            emailField.setText(userName);
            registerAccountBtn.setVisibility(View.GONE);
            passwordField.requestFocus();

            if (isFingerprintAvailable) {
                fingerprintBtn.setVisibility(View.VISIBLE);
            }

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
                authorizationViewModel.setAuthValue(createEmptyModel(AuthorizeType.AUTH_BIOMETRIC_LOGIN));
            }
        });

        titleLabel.setText(getString(R.string.title_login));

        registerAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegistrationUIListener.onShowRegistrationUI();
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
                authorizationViewModel.setAuthValue(createModel(AuthorizeType.AUTH_PASSWORD_LOGIN));
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
        fingerprintBtn = binding.fingerprintLayout.btnFingerprint;

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

    public interface ShowRegistrationUIListener {
        void onShowRegistrationUI();
    }

}
