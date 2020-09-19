package com.example.notes.test.ui.dialogs;

import android.content.Context;
import com.example.notes.test.ui.dialogs.impl.AlertDialog;
import com.example.notes.test.ui.dialogs.impl.BaseDialog;

public class DialogHelper {

    public static BaseDialog createDialog(int type, Context context) {
        return new AlertDialog(type, context);
    }

}
