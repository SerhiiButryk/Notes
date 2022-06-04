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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.serhii.apps.notes.R;
import com.serhii.core.utils.GoodUtils;

public class DialogWithEnterFieled extends DialogFragment {

    public static final String EXTRA_TITLE_TEXT =  "dialog title";
    public static final String EXTRA_HINT_TEXT =  "dialog hint";

    private Button okButton;
    private EditText editTextField;
    private TextChecker textChecker;
    private DialogListener listener;

    public DialogWithEnterFieled(DialogListener listener) {
        this.listener = listener;
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
        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.dialog_with_input_field, null);

        Bundle bundle = getArguments();

        TextView title = dialogView.findViewById(R.id.title_dialog);
        title.setText(bundle.getString(EXTRA_TITLE_TEXT));

        editTextField = dialogView.findViewById(R.id.edit_text_field);
        editTextField.setHint(bundle.getString(EXTRA_HINT_TEXT));
        editTextField.requestFocus();

        okButton = dialogView.findViewById(R.id.btn_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onOkClicked(GoodUtils.getText(editTextField));
                    dismiss();
                }
            }
        });
        // By default
        okButton.setEnabled(false);

        textChecker = new TextChecker(editTextField, okButton);
        editTextField.addTextChangedListener(textChecker);

        Button cancelButton = dialogView.findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCancelClicked();
                }
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

    public interface DialogListener {
        void onOkClicked(String enteredText);
        void onCancelClicked();
    }

}
