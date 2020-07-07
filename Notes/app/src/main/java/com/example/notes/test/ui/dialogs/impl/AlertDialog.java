package com.example.notes.test.ui.dialogs.impl;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

import com.example.notes.test.R;
import com.example.notes.test.common.AppUtils;
import com.example.notes.test.control.NativeBridge;
import com.example.notes.test.control.logic.AuthResult;
import com.example.notes.test.ui.AlertDialogUI;

public class AlertDialog implements BaseDialog {

    private int type;

    private String title;
    private String message;

    private static final String DIALOG_TAG =  "alert dialog";

    public AlertDialog(int type, Context context) {
        this.type = type;

        title = "";
        message = "";

        setStrings(context);
    }

    public static void showDialog(FragmentActivity activity, int type) {
        AlertDialogUI dialog = new AlertDialogUI();

        Bundle b = new Bundle();
        b.putInt(AlertDialogUI.DIALOG_TYPE_EXTERN, type);
        dialog.setArguments(b);

        dialog.show(activity.getSupportFragmentManager(), DIALOG_TAG);
    }

    private void setStrings(Context context) {

        if (type == AuthResult.ACCOUNT_INVALID.getTypeId()) {

            title = context.getString(R.string.title_no_user_account);
            message = context.getString(R.string.ms_no_user_account);

        } else if (type == AuthResult.WRONG_PASSWORD.getTypeId()) {

            NativeBridge nativeBridge = new NativeBridge();
            int limitLeft = nativeBridge.getLimitLeft();

            title = context.getString(R.string.title_wrong_password);
            message = AppUtils.formatString(context.getString(R.string.ms_wrong_password), limitLeft);

        } else if (type == AuthResult.EMPTY_FIELD.getTypeId()) {

            title = context.getString(R.string.title_empty_field);
            message = context.getString(R.string.ms_empty_field);

        } else if (type == AuthResult.USER_NAME_EXISTS.getTypeId()) {

            title = context.getString(R.string.title_user_exists);
            message = context.getString(R.string.ms_user_exists);

        } else if (type == AuthResult.PASSWORD_DIFFERS.getTypeId()) {

            title = context.getString(R.string.title_password_differs);
            message = context.getString(R.string.ms_password_differs);

        } else if (type == AuthResult.SPACE_CONTAIN.getTypeId()) {

            title = context.getString(R.string.title_space_contain);
            message = context.getString(R.string.ms_space_contain);

        } else if (type == AuthResult.UNLOCK_KEY_INVALID.getTypeId()) {

            title = context.getString(R.string.title_unlock_key_invalid);
            message = context.getString(R.string.ms_unlock_key_invalid);
        }

    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public boolean hasCustomLayout() {
        return false;
    }

}
