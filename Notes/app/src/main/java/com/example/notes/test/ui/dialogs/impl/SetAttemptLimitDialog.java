package com.example.notes.test.ui.dialogs.impl;

import androidx.fragment.app.FragmentActivity;

import com.example.notes.test.ui.SetLoginLimitDialogUI;

public class SetAttemptLimitDialog {

    private static final String DIALOG_TAG =  "enter number dialog";

    public static void showDialog(FragmentActivity activity) {
        SetLoginLimitDialogUI dialog = new SetLoginLimitDialogUI();
        dialog.show(activity.getSupportFragmentManager(), DIALOG_TAG);
    }
}
