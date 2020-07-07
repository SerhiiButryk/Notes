package com.example.notes.test.ui;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
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

import com.example.notes.test.R;
import com.example.notes.test.control.NativeBridge;
import com.example.core.utils.GoodUtils;

public class ChangePasswordDialogUI extends DialogFragment {

    private Button okButton;
    private EditText oldPassword;
    private EditText newPassword;

    private TextChecker textChecker;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        ViewGroup viewGroup = initView(getActivity().getLayoutInflater());

        builder.setView(viewGroup);

        return builder.create();
    }

    private ViewGroup initView(LayoutInflater inflater) {
        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.change_password_dialog, null);

        TextView title = dialogView.findViewById(R.id.title_dialog);
        title.setText(inflater.getContext().getString(R.string.change_password_title_dialog));

        oldPassword = dialogView.findViewById(R.id.old_password_field);
        oldPassword.requestFocus();

        newPassword = dialogView.findViewById(R.id.new_password_field);

        okButton = dialogView.findViewById(R.id.btn_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check entered password
                NativeBridge nativeBridge = new NativeBridge();
                boolean success = nativeBridge.verifyPassword(GoodUtils.getText(oldPassword));

                if (!success) {
                    Toast.makeText(ChangePasswordDialogUI.this.getContext().getApplicationContext(),
                            getString(R.string.change_password_toast_not_correct_password), Toast.LENGTH_LONG).show();
                } else {
                    boolean result = nativeBridge.setNewPassword(GoodUtils.getText(newPassword));

                    if (result) {
                        Toast.makeText(ChangePasswordDialogUI.this.getContext().getApplicationContext(),
                                getString(R.string.change_password_toast_password_set), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ChangePasswordDialogUI.this.getContext().getApplicationContext(),
                                getString(R.string.change_password_toast_password_error), Toast.LENGTH_LONG).show();
                    }

                    dismiss();
                }
            }
        });
        // By default
        okButton.setEnabled(false);

        textChecker = new TextChecker(oldPassword, newPassword, okButton);
        oldPassword.addTextChangedListener(textChecker);
        newPassword.addTextChangedListener(textChecker);

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

        private EditText oldPassword;
        private EditText newPassword;
        private Button okButton;

        public TextChecker(EditText oldPassword, EditText newPassword, Button okButton) {
            this.oldPassword = oldPassword;
            this.newPassword = newPassword;
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
            boolean enable = !TextUtils.isEmpty(oldPassword.getText())
                    && !TextUtils.isEmpty(newPassword.getText());

            okButton.setEnabled(enable);
        }

    }
}
