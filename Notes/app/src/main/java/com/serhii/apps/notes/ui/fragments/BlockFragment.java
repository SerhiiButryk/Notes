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
import com.serhii.apps.notes.ui.utils.TextChecker;
import com.serhii.apps.notes.ui.view_model.LoginViewModel;
import com.serhii.core.security.Hash;
import com.serhii.core.utils.GoodUtils;

public class BlockFragment extends Fragment {

    private EditText accessKeyField;
    private LoginViewModel loginViewModel;

    private final EditText.OnEditorActionListener keyEventActionDone = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event)  {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                unlockApplication();
                return true;
            }
            return false;
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Retrieve an instance of ViewModel
        loginViewModel = new ViewModelProvider(getActivity()).get(LoginViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initView(inflater, container);
    }

    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_block_view, container, false);

        accessKeyField = view.findViewById(R.id.access_key);
        Button ok = view.findViewById(R.id.btn_login);
        ok.setEnabled(false);

        accessKeyField.addTextChangedListener(new TextChecker(accessKeyField, ok));
        accessKeyField.setOnEditorActionListener(keyEventActionDone);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unlockApplication();
            }
        });

        ok.setVisibility(View.VISIBLE);
        view.findViewById(R.id.email_layout).setVisibility(View.VISIBLE);
        return view;
    }

    private void unlockApplication() {
        Hash hash = new Hash();
        AuthModel authModel = new AuthModel("",
                hash.hashMD5(GoodUtils.getText(accessKeyField)),
                "",
                AuthorizeType.AUTH_UNLOCK);
        // For safety
        accessKeyField.setText("");
        // Set data
        loginViewModel.setAuthValue(authModel);
    }

}
