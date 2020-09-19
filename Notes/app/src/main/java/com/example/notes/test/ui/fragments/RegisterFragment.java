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
import android.widget.TextView;

import com.example.notes.test.AuthorizationActivity;
import com.example.notes.test.R;
import com.example.notes.test.databinding.FragmentRegistrationViewBinding;
import com.example.notes.test.ui.data_model.AuthModel;
import com.example.notes.test.ui.view_model.AuthViewModel;
import com.example.core.utils.GoodUtils;

public class RegisterFragment extends Fragment {

    private  EditText emailField;
    private  TextView titleLabel;
    private  EditText passwordField;
    private  EditText confirmPasswordField;
    private  Button registerButton;

    private OnRegisterListener registerListener;
    private AuthViewModel authViewModel;

    private EditText.OnEditorActionListener keyEventActionDone = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event)  {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Set data
                authViewModel.changeValue(getData());

                registerListener.onRegisterClicked();
                return true;
            }
            return false;
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Retrieve an instance on ViewModel
        authViewModel = ViewModelProviders.of(getActivity()).get(AuthViewModel.class);

        AuthorizationActivity activity = (AuthorizationActivity) getActivity();
        registerListener = activity.getObserver();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = initBinding(inflater, container);

        titleLabel.setText(getString(R.string.title_reg));

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set data
                authViewModel.changeValue(getData());

                registerListener.onRegisterClicked();
            }
        });

        emailField.requestFocus();
        confirmPasswordField.setOnEditorActionListener(keyEventActionDone);

        return view;
    }

    private View initBinding(LayoutInflater inflater, ViewGroup viewGroup) {
        FragmentRegistrationViewBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_registration_view, viewGroup, false);

        // Set references
        emailField = binding.usrEmail;
        titleLabel = binding.title;
        passwordField = binding.usrPassword;
        confirmPasswordField = binding.confirmPassword;
        registerButton = binding.btnRegister;

        return binding.getRoot();
    }

    private AuthModel getData() {
        AuthModel authModel = new AuthModel();
        authModel.setPassword(GoodUtils.getText(passwordField));
        authModel.setEmail(GoodUtils.getText(emailField));
        authModel.setConfirmPassword(GoodUtils.getText(confirmPasswordField));

        return authModel;
    }

    /**
     *  Callback interface to the activity
     */
    public interface OnRegisterListener {

        void onRegisterClicked();
    }

}
