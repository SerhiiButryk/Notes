/*
 * Copyright 2023. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.fragments;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.serhii.apps.notes.R;
import com.serhii.apps.notes.control.auth.types.AuthorizeType;
import com.serhii.apps.notes.ui.data_model.AuthModel;
import com.serhii.apps.notes.ui.view_model.LoginViewModel;
import com.serhii.core.utils.GoodUtils;

public class RegisterFragment extends Fragment {

    public static final String FRAGMENT_TAG = "RegisterFragmentTag";

    private  EditText emailField;
    private  TextView titleField;
    private  EditText passwordField;
    private  EditText confirmPasswordField;
    private  Button registerButton;

    private LoginViewModel loginViewModel;

    private final EditText.OnEditorActionListener keyEventActionDone = new TextView.OnEditorActionListener() {
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

        View view = initView(inflater, container);

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

    public View initView(LayoutInflater inflater, ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.fragment_registration_view, viewGroup, false);
        // Set references
        emailField = view.findViewById(R.id.usr_email);
        titleField = view.findViewById(R.id.title);
        passwordField = view.findViewById(R.id.usr_password);
        confirmPasswordField = view.findViewById(R.id.confirm_password);
        registerButton = view.findViewById(R.id.btn_register);

        view.findViewById(R.id.textInputLayout).setVisibility(View.VISIBLE);
        view.findViewById(R.id.textInputLayout2).setVisibility(View.VISIBLE);
        view.findViewById(R.id.textInputLayout3).setVisibility(View.VISIBLE);

        registerButton.setVisibility(View.VISIBLE);
        return view;
    }

    private AuthModel createModel(AuthorizeType type) {
        return new AuthModel(GoodUtils.getText(emailField),
                GoodUtils.getText(passwordField),
                GoodUtils.getText(confirmPasswordField),
                type);
    }

}
