package com.example.notes.test.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;

import com.example.notes.test.R;
import com.example.notes.test.ui.dialogs.impl.BaseDialog;
import com.example.notes.test.ui.dialogs.DialogHelper;

/**
 *  Simple dialog with title and message and no UI controls
 */

public class AlertDialogUI extends DialogFragment {

    public static String DIALOG_TYPE_EXTERN = "dialog type";

    private int typeDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() == null) {
            throw new IllegalArgumentException("No parameters needed for initialization");
        }

        typeDialog =  getArguments().getInt(DIALOG_TYPE_EXTERN, 0);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        BaseDialog dialog = DialogHelper.createDialog(typeDialog, getContext().getApplicationContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setPositiveButton(R.string.kbtn_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            }).setTitle(dialog.getTitle());

        builder.setMessage(dialog.getMessage());

        return builder.create();
    }

}
