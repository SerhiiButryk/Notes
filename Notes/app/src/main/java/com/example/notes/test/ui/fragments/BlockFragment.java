package com.example.notes.test.ui.fragments;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.core.security.Hash;
import com.example.core.utils.GoodUtils;
import com.example.notes.test.R;
import com.example.notes.test.control.logic.AuthorizeType;
import com.example.notes.test.databinding.FragmentBlockViewBinding;
import com.example.notes.test.ui.data_model.AuthModel;
import com.example.notes.test.ui.utils.TextChecker;
import com.example.notes.test.ui.view_model.AuthViewModel;

public class BlockFragment extends Fragment {

    private EditText accessKeyField;
    private Button ok;

    private LoginFragment.LoginListener loginListener;

    private AuthViewModel authViewModel;

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
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            loginListener = (LoginFragment.LoginListener) context;
        } catch (ClassCastException exception) {
            throw new ClassCastException(context.toString() + " must implement LoginListener interface");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();

        loginListener = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Retrieve an instance of ViewModel
        authViewModel = ViewModelProviders.of(getActivity()).get(AuthViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = initBinding(inflater, container);

        return view;
    }

    private View initBinding(LayoutInflater inflater, ViewGroup viewGroup) {

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

        return binding.getRoot();
    }

    private void unlockApplication() {

        Hash hash = new Hash();
        AuthModel authModel = new AuthModel();

        String enteredKey = GoodUtils.getText(accessKeyField);

        authModel.setAuthType(AuthorizeType.AUTH_UNLOCK);
        authModel.setPassword(hash.hashMD5(enteredKey));

        authViewModel.changeValue(authModel);

        // Proceed with unlock
        loginListener.onLoginRequested();
    }

}
