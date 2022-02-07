package com.serhii.apps.notes.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.serhii.apps.notes.R;
import com.serhii.apps.notes.control.NativeBridge;
import com.serhii.core.utils.GoodUtils;

public class SetPasswordDialogUI extends DialogFragment {

    private Button okButton;
    private EditText password;

    private TextChecker textChecker;

    private OnPasswordSetListener passwordSetListener;

    public SetPasswordDialogUI(OnPasswordSetListener passwordSetListener) {
        this.passwordSetListener = passwordSetListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        ViewGroup viewGroup = initView(getActivity().getLayoutInflater());

        builder.setView(viewGroup);

        return builder.create();
    }

    @SuppressLint("InflateParams")
    private ViewGroup initView(LayoutInflater inflater) {
        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.set_password_dialog, null);

        TextView title = dialogView.findViewById(R.id.title_dialog);
        title.setText(inflater.getContext().getString(R.string.set_password_dialog_title));

        password = dialogView.findViewById(R.id.edit_text_field);
        password.requestFocus();

        okButton = dialogView.findViewById(R.id.btn_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordSetListener != null) {
                    passwordSetListener.onPasswordSet(GoodUtils.getText(password));
                    dismiss();
                }
            }
        });
        // By default
        okButton.setEnabled(false);

        textChecker = new TextChecker(password, okButton);
        password.addTextChangedListener(textChecker);

        Button cancelButton = dialogView.findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return dialogView;
    }

    private static class TextChecker implements TextWatcher {

        private EditText password;
        private Button okButton;

        public TextChecker(EditText password, Button okButton) {
            this.password = password;
            this.okButton = okButton;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            boolean enable = !TextUtils.isEmpty(password.getText());
            okButton.setEnabled(enable);
        }

    }

    public interface OnPasswordSetListener {
        void onPasswordSet(String password);
    }

}
