package com.serhii.apps.notes.ui.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.serhii.apps.notes.R;
import com.serhii.apps.notes.ui.ChangePasswordDialogUI;
import com.serhii.apps.notes.ui.SetPasswordDialogUI;
import com.serhii.apps.notes.ui.dialogs.base.AlertDialogHelper;

public class DialogHelper {

    private static final String CPD_DIALOG_TAG =  "Change password dialog";
    private static final String SPD_DIALOG_TAG =  "Set password dialog";

    public static final int ALERT_DIALOG_TYPE_BACKUP_ERROR = 101;
    public static final int ALERT_DIALOG_TYPE_PASSWORD_IS_WEAK = 102;

    public static void showChangePasswordDialog(FragmentActivity activity) {
        ChangePasswordDialogUI dialog = new ChangePasswordDialogUI();
        dialog.show(activity.getSupportFragmentManager(), CPD_DIALOG_TAG);
    }

    public static void showSetPasswordDialog(FragmentActivity activity, SetPasswordDialogUI.OnPasswordSetListener listener) {
        SetPasswordDialogUI dialog = new SetPasswordDialogUI(listener);
        dialog.show(activity.getSupportFragmentManager(), SPD_DIALOG_TAG);
    }

    public static void showConfirmDialog(Activity activity, final ConfirmDialogCallback callback, int title, int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setPositiveButton(activity.getResources().getString(R.string.kbtn_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (callback != null) {
                    callback.onOkClicked();
                }
            }
        });

        builder.setNegativeButton(activity.getResources().getString(R.string.kbtn_cancel), new DialogInterface.OnClickListener() {
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

    public static void showAlertDialog(int type, Context context) {

        AlertDialogHelper dialog = new AlertDialogHelper(type, context);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setPositiveButton(context.getResources().getString(R.string.kbtn_ok), new DialogInterface.OnClickListener() {
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
