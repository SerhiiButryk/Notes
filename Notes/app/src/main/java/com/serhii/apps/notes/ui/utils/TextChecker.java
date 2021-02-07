package com.serhii.apps.notes.ui.utils;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

public class TextChecker implements TextWatcher {

    private EditText controlField;
    private Button controlButton;

    public TextChecker(EditText controlField, Button controlButton) {
        this.controlField = controlField;
        this.controlButton = controlButton;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        boolean enable = !TextUtils.isEmpty(controlField.getText());
        controlButton.setEnabled(enable);
    }

}
