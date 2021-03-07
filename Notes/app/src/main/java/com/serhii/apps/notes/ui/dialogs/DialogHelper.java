package com.serhii.apps.notes.ui.dialogs;

import android.app.Activity;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.serhii.apps.notes.ui.ChangePasswordDialogUI;
import com.serhii.apps.notes.ui.dialogs.base.AlertDialogHelper;

public class DialogHelper {

    private static final String CPD_DIALOG_TAG =  "Change password dialog";

    public static void showChangePasswordDialog(FragmentActivity activity) {
        ChangePasswordDialogUI dialog = new ChangePasswordDialogUI();
        dialog.show(activity.getSupportFragmentManager(), CPD_DIALOG_TAG);
    }

    public static void showConfirmDialog(Activity activity, final ConfirmDialogCallback callback, int title, int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (callback != null) {
                    callback.onOkClicked();
                }
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (callback != null) {
                    callback.onCancelClicked();
                }
            }
        });

        builder.setTitle(title);

        builder.setMessage(message);

        builder.create().show();
    }

    public static void showAlertDialog(int type, Activity activity) {

        AlertDialogHelper dialog = new AlertDialogHelper(type, activity);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // no-op
            }
        });

        builder.setTitle(dialog.getTitle());
        builder.setMessage(dialog.getMessage());

        builder.show();
    }

    public interface ConfirmDialogCallback {

        void onOkClicked();

        void onCancelClicked();
    }

}
