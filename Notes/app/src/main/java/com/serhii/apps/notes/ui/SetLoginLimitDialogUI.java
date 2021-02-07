package com.serhii.apps.notes.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.serhii.apps.notes.R;
import com.serhii.apps.notes.control.NativeBridge;
import com.serhii.apps.notes.ui.utils.TextChecker;

public class SetLoginLimitDialogUI extends DialogFragment {

    private static final Integer upperLimit = 10;
    private static final Integer lowerLimit = 1;

    private OnNewValueSet newValueSetListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            newValueSetListener = (OnNewValueSet) context;
        } catch (ClassCastException exception) {
            throw new ClassCastException(context.toString() + " must implement OnNewValueSet interface");
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        ViewGroup viewGroup = initView(getActivity().getLayoutInflater());

        builder.setView(viewGroup);

        return builder.create();
    }

    private ViewGroup initView(LayoutInflater inflater) {

        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.change_password_dialog, null);

        TextView title = dialogView.findViewById(R.id.title_dialog);
        title.setText(getString(R.string.set_limit_Title));

        final EditText attemptLimitField = dialogView.findViewById(R.id.old_password_field);
        attemptLimitField.setHint(getString(R.string.set_limit_hint));
        attemptLimitField.requestFocus();

        dialogView.findViewById(R.id.new_password_field).setVisibility(View.GONE);

        Button ok = dialogView.findViewById(R.id.btn_ok);
        ok.setEnabled(false);

        attemptLimitField.addTextChangedListener(new TextChecker(attemptLimitField, ok));

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String number = attemptLimitField.getText().toString();

                if (!number.isEmpty() && TextUtils.isDigitsOnly(number)
                        && (Integer.parseInt(number) > lowerLimit && Integer.parseInt(number) <= upperLimit)) {

                    int val = Integer.parseInt(number);

                    NativeBridge nativeBridge = new NativeBridge();
                    nativeBridge.setAttemptLimit(val);

                    newValueSetListener.onNewValueSet();

                    dismiss();

                } else {

                    Toast.makeText(SetLoginLimitDialogUI.this.getContext(), getString(R.string.set_limit_error), Toast.LENGTH_LONG).show();
                }
            }
        });

        Button cancel = dialogView.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();;
            }
        });

        return dialogView;
    }

    public interface OnNewValueSet {

        void onNewValueSet();
    }

}
