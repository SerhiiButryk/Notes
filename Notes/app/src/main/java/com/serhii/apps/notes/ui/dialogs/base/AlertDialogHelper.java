package com.serhii.apps.notes.ui.dialogs.base;

import android.content.Context;

import com.serhii.core.utils.GoodUtils;
import com.serhii.apps.notes.R;
import com.serhii.apps.notes.control.NativeBridge;
import com.serhii.apps.notes.control.types.AuthResult;

public class AlertDialogHelper {

    private int type;

    private String title;
    private String message;

    public AlertDialogHelper(int type, Context context) {
        this.type = type;

        title = "";
        message = "";

        setStrings(context);
    }

    private void setStrings(Context context) {

        if (type == AuthResult.ACCOUNT_INVALID.getTypeId()) {

            title = context.getString(R.string.title_no_user_account);
            message = context.getString(R.string.ms_no_user_account);

        } else if (type == AuthResult.WRONG_PASSWORD.getTypeId()) {

            NativeBridge nativeBridge = new NativeBridge();
            int limitLeft = nativeBridge.getLimitLeft();

            title = context.getString(R.string.title_wrong_password);
            message = GoodUtils.formatString(context.getString(R.string.ms_wrong_password), limitLeft);

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

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

}
