package com.serhii.apps.notes.ui.fragments;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

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
import android.widget.TextView;

import com.serhii.apps.notes.R;
import com.serhii.apps.notes.control.types.AuthorizeType;
import com.serhii.apps.notes.databinding.FragmentRegistrationViewBinding;
import com.serhii.apps.notes.ui.data_model.AuthModel;
import com.serhii.apps.notes.ui.fragments.base.IViewBindings;
import com.serhii.apps.notes.ui.view_model.LoginViewModel;
import com.serhii.core.utils.GoodUtils;

public class RegisterFragment extends Fragment implements IViewBindings {

    public static final String FRAGMENT_TAG = "RegisterFragmentTag";

    private  EditText emailField;
    private  TextView titleField;
    private  EditText passwordField;
    private  EditText confirmPasswordField;
    private  Button registerButton;

    private LoginViewModel loginViewModel;

    private EditText.OnEditorActionListener keyEventActionDone = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event)  {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Set data
                loginViewModel.setAuthValue(createModel(AuthorizeType.AUTH_REGISTRATION));
                return true;
            }
            return false;
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Retrieve an instance on ViewModel
        loginViewModel = new ViewModelProvider(getActivity()).get(LoginViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = initBinding(inflater, container);

        titleField.setText(getString(R.string.title_reg));

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set data
                loginViewModel.setAuthValue(createModel(AuthorizeType.AUTH_REGISTRATION));
            }
        });

        emailField.requestFocus();
        confirmPasswordField.setOnEditorActionListener(keyEventActionDone);

        return view;
    }

    @Override
    public View initBinding(LayoutInflater inflater, ViewGroup viewGroup) {
        FragmentRegistrationViewBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_registration_view, viewGroup, false);

        // Set references
        emailField = binding.usrEmail;
        titleField = binding.title;
        passwordField = binding.usrPassword;
        confirmPasswordField = binding.confirmPassword;
        registerButton = binding.btnRegister;

        binding.textInputLayout.setVisibility(View.VISIBLE);
        binding.textInputLayout2.setVisibility(View.VISIBLE);
        binding.textInputLayout3.setVisibility(View.VISIBLE);

        registerButton.setVisibility(View.VISIBLE);

        return binding.getRoot();
    }

    private AuthModel createModel(AuthorizeType type) {
        AuthModel authModel = new AuthModel();
        authModel.setPassword(GoodUtils.getText(passwordField));
        authModel.setEmail(GoodUtils.getText(emailField));
        authModel.setConfirmPassword(GoodUtils.getText(confirmPasswordField));
        authModel.setAuthType(type);
        return authModel;
    }

}
