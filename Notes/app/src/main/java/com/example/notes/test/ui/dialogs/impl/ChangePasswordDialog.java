package com.example.notes.test.ui.dialogs.impl;

import androidx.fragment.app.FragmentActivity;

import com.example.notes.test.ui.ChangePasswordDialogUI;

public class ChangePasswordDialog {

    private static final String DIALOG_TAG =  "Change password dialog";

    public static void showDialog(FragmentActivity activity) {
        ChangePasswordDialogUI dialog = new ChangePasswordDialogUI();
        dialog.show(activity.getSupportFragmentManager(), DIALOG_TAG);
    }

}
