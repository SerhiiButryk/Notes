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
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.serhii.core.security.Hash;
import com.serhii.core.utils.GoodUtils;
import com.serhii.apps.notes.R;
import com.serhii.apps.notes.control.types.AuthorizeType;
import com.serhii.apps.notes.databinding.FragmentBlockViewBinding;
import com.serhii.apps.notes.ui.data_model.AuthModel;
import com.serhii.apps.notes.ui.fragments.base.IViewBindings;
import com.serhii.apps.notes.ui.utils.TextChecker;
import com.serhii.apps.notes.ui.view_model.LoginViewModel;

public class BlockFragment extends Fragment implements IViewBindings {

    private EditText accessKeyField;
    private Button ok;

    private LoginViewModel loginViewModel;

    private EditText.OnEditorActionListener keyEventActionDone = new TextView.OnEditorActionListener() {
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

        return initBinding(inflater, container);
    }

    @Override
    public View initBinding(LayoutInflater inflater, ViewGroup viewGroup) {

        FragmentBlockViewBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_block_view, viewGroup, false);

        accessKeyField = binding.accessKey;
        ok = binding.btnLogin;

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
        binding.emailLayout.setVisibility(View.VISIBLE);

        return binding.getRoot();
    }

    private void unlockApplication() {

        Hash hash = new Hash();
        AuthModel authModel = new AuthModel();
        authModel.setAuthType(AuthorizeType.AUTH_UNLOCK);
        authModel.setPassword(hash.hashMD5(GoodUtils.getText(accessKeyField)));
        // For safety
        accessKeyField.setText("");

        // Set data
        loginViewModel.setAuthValue(authModel);
    }

}
