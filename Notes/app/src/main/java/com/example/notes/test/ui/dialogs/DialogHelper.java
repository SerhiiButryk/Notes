package com.example.notes.test.ui.dialogs;

import android.content.Context;
import com.example.notes.test.ui.dialogs.impl.AlertDialog;
import com.example.notes.test.ui.dialogs.impl.BaseDialog;
import com.example.notes.test.ui.dialogs.impl.ChangePasswordDialog;

public class DialogHelper {

    public static final int TYPE_CHANGE_PASSWORD_DIALOG = 1;

    public static BaseDialog createDialog(int type, Context context) {
        if (type == TYPE_CHANGE_PASSWORD_DIALOG) {
            return new ChangePasswordDialog(type, context);
        } else {
            return new AlertDialog(type, context);
        }
    }

}
