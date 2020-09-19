package com.example.notes.test.ui.fragments;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

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

import com.example.core.security.Hash;
import com.example.notes.test.AuthorizationActivity;
import com.example.notes.test.R;
import com.example.notes.test.control.NativeBridge;
import com.example.notes.test.control.managers.BiometricAuthManager;
import com.example.notes.test.databinding.FragmentLoginViewBinding;
import com.example.notes.test.ui.data_model.AuthModel;
import com.example.notes.test.ui.view_model.AuthViewModel;
import com.example.core.utils.GoodUtils;

public class LoginFragment extends Fragment {

    private static String TAG = LoginFragment.class.getSimpleName();

    private LoginListener loginListener;
    private AuthViewModel authViewModel;

    private EditText emailField;
    private EditText passwordField;
    private Button loginButton;
    private TextView registerAccountBtn;
    private TextView titleLabel;
    private ImageButton fingerprintBtn;

    private BiometricAuthManager biometricAuthManager = new BiometricAuthManager();
    private boolean isFingerprintAvailable;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    private EditText.OnEditorActionListener keyEventActionDone = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event)  {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Set data
                authViewModel.changeValue(getData());

                // Send event
                loginListener.onLoginClicked();
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
        authViewModel = ViewModelProviders.of(getActivity()).get(AuthViewModel.class);

        AuthorizationActivity activity = (AuthorizationActivity) getActivity();
        loginListener = activity.getObserver();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = initBinding(inflater, container);

        final NativeBridge nativeBridge = new NativeBridge();
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

        biometricAuthManager.setOnAuthenticateListener(new BiometricAuthManager.OnAuthenticateListener() {
            @Override
            public void onSuccess() {
                loginListener.onFingerPrintLoginClicked();
            }
        });

        titleLabel.setText(getString(R.string.title_login));

        registerAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginListener.onRegisterNewAccountClicked();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set data
                authViewModel.changeValue(getData());

                // Send event
                loginListener.onLoginClicked();
            }
        });

        passwordField.setOnEditorActionListener(keyEventActionDone);

        return view;
    }

    public void onUserAccountCreated() {
        registerAccountBtn.setVisibility(View.GONE);

        NativeBridge nativeBridge = new NativeBridge();
        String userName = nativeBridge.getUserName();

        emailField.setText(userName);
        passwordField.requestFocus();
    }

    private View initBinding(LayoutInflater inflater, ViewGroup viewGroup) {
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

    private AuthModel getData() {

        Hash hash = new Hash();

        AuthModel authModel = new AuthModel();

        authModel.setPassword(hash.hashMD5(GoodUtils.getText(passwordField)));
        authModel.setEmail(GoodUtils.getText(emailField));

        emailField.setText(null);
        passwordField.setText(null);

        return authModel;
    }

    /**
     *  Callback interface to the activity
     */
    public interface LoginListener {

        void onRegisterNewAccountClicked();

        void onLoginClicked();

        void onFingerPrintLoginClicked();
    }
}
