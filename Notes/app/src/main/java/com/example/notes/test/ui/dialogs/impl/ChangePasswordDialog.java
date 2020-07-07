package com.example.notes.test.ui.dialogs.impl;

import android.content.Context;
import androidx.fragment.app.FragmentActivity;

import com.example.notes.test.R;
import com.example.notes.test.ui.ChangePasswordDialogUI;

public class ChangePasswordDialog implements BaseDialog {

    private static final String DIALOG_TAG =  "enter text dialog";

    private String title;
    private Context context;
    private int type;

    public static void showDialog(FragmentActivity activity) {
        ChangePasswordDialogUI dialog = new ChangePasswordDialogUI();
        dialog.show(activity.getSupportFragmentManager(), DIALOG_TAG);
    }

    public ChangePasswordDialog(int type, Context context) {
        this.context = context;
        this.type = type;

        setStrings(type);
    }

    private void setStrings(int type) {
        context.getString(R.string.change_password_title_dialog);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public boolean hasCustomLayout() {
        return true;
    }

}
