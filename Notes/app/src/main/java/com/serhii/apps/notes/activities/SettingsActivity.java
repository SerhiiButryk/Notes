package com.serhii.apps.notes.activities;

import static com.serhii.apps.notes.control.managers.BackupManager.REQUEST_CODE_BACKUP_NOTES;
import static com.serhii.apps.notes.control.managers.BackupManager.REQUEST_CODE_OPEN_BACKUP_FILE;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.serhii.apps.notes.R;
import com.serhii.apps.notes.control.managers.BackupManager;
import com.serhii.apps.notes.ui.fragments.SettingsFragment;
import com.serhii.core.log.Log;
import com.serhii.core.utils.GoodUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SettingsActivity extends AppBaseActivity {

    private static final String TAG = "SettingsActivity";
    private static final String SETTINGS_FRAGMENT_TAG = "main settings";

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (savedInstanceState == null) {
            addFragment();
        }

        setActionBar();
    }

    private void addFragment() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true) // Needed for optimization
                .replace(R.id.container, new SettingsFragment(), SETTINGS_FRAGMENT_TAG)
                .commit();
    }

    private void setActionBar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.preference_title));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if (BackupManager.REQUEST_CODE_EXTRACT_NOTES == requestCode && resultCode == RESULT_OK) {

                Log.info(TAG, "onActivityResult() got result for REQUEST_CODE_EXTRACT_NOTES");

                if (data != null) {
                    extractNotes(data);
                } else {
                    // Should not happen
                    Log.info(TAG, "onActivityResult() 1");
                }

            } else if (requestCode == REQUEST_CODE_BACKUP_NOTES && resultCode == RESULT_OK) {

                Log.info(TAG, "onActivityResult() got result for REQUEST_CODE_BACKUP_NOTES");

                if (data != null) {
                    backupNotes(data);
                } else {
                    // Should not happen
                    Log.info(TAG, "onActivityResult() 2");
                }

            } else if (requestCode == REQUEST_CODE_OPEN_BACKUP_FILE && resultCode == RESULT_OK) {

                Log.info(TAG, "onActivityResult() got result for REQUEST_CODE_OPEN_BACKUP_FILE");

                if (data != null) {
                    restoreNotes(data);
                } else {
                    // Should not happen
                    Log.info(TAG, "onActivityResult() 3");
                }

            }

        }

        private void extractNotes(Intent data) {
            OutputStream outputStream = null;
            try {
                outputStream = getContentResolver().openOutputStream(data.getData());
            } catch (FileNotFoundException e) {
                Log.error(TAG, "extractNotes() error: " + e);
                e.printStackTrace();
                return;
            }

            boolean result = BackupManager.getInstance().saveDataAsPlainText(outputStream, this);
            if (result) {
                GoodUtils.showToast(this, R.string.result_success);
            } else {
                GoodUtils.showToast(this, R.string.result_failed);
            }
        }

        private void backupNotes(Intent data) {
            Log.info(TAG, "backupNotes() IN");

            OutputStream outputStream = null;
            try {
                outputStream = getContentResolver().openOutputStream(data.getData());
            } catch (FileNotFoundException e) {
                Log.error(TAG, "onOkClicked() error: " + e);
                e.printStackTrace();
                return;
            }

            boolean result = BackupManager.getInstance().backupData(outputStream, SettingsActivity.this);
            if (result) {
                GoodUtils.showToast(SettingsActivity.this, R.string.result_success);
            } else {
                GoodUtils.showToast(SettingsActivity.this, R.string.result_failed);
            }

            // TODO: This doesn't work because of issue with symmetric encryption
            // Localized strings
//            String hint = getString(R.string.set_password_dialog_title);
//            String title = getString(R.string.set_password_dialog_title);

            // Ask for password
//            DialogHelper.showDialogWithEnterField(this, new DialogWithEnterFieled.DialogListener() {
//
//                @Override
//                public void onOkClicked(String enteredText) {
//                    Log.info(TAG, "onOkClicked() IN");
//                    OutputStream outputStream = null;
//                    try {
//                        outputStream = getContentResolver().openOutputStream(data.getData());
//                    } catch (FileNotFoundException e) {
//                        Log.error(TAG, "onOkClicked() error: " + e);
//                        e.printStackTrace();
//                        return;
//                    }
//
//                    Hash hash = new Hash();
//                    String pass = hash.hashMD5(enteredText);
//
//                    NativeBridge nativeBridge = new NativeBridge();
//                    if (!nativeBridge.verifyPassword(pass)) {
//                        GoodUtils.showToast(SettingsActivity.this, R.string.wrong_pass);
//                        Log.error(TAG, "onOkClicked() wrong pass");
//                        return;
//                    }
//
//                    boolean result = BackupManager.getInstance().backupData(outputStream, SettingsActivity.this, hash.hashMD5(pass));
//                    if (result) {
//                        GoodUtils.showToast(SettingsActivity.this, R.string.result_success);
//                    } else {
//                        GoodUtils.showToast(SettingsActivity.this, R.string.result_failed);
//                    }
//                    Log.info(TAG, "onOkClicked() OUT");
//                }
//
//                @Override
//                public void onCancelClicked() {
//                    Log.info(TAG, "onCanceledClicked() IN");
//                    GoodUtils.showToast(SettingsActivity.this, R.string.result_canceled);
//                }
//
//            }, title, hint);

        }

        private void restoreNotes(Intent data) {
            Log.info(TAG, "restoreNotes() IN");

            String json = readBackupFile(data);

            boolean result = BackupManager.getInstance().restoreData(json, SettingsActivity.this);
            if (result) {
                GoodUtils.showToast(SettingsActivity.this, R.string.result_success);
            } else {
                GoodUtils.showToast(SettingsActivity.this, R.string.result_failed);
            }

            // TODO: This doesn't work because of issue with symmetric encryption
            // Localized strings
//            String hint = getString(R.string.set_password_dialog_title);
//            String title = getString(R.string.set_password_dialog_title2);

            // Ask for password
//            DialogHelper.showDialogWithEnterField(this, new DialogWithEnterFieled.DialogListener() {
//                @Override
//                public void onOkClicked(String enteredText) {
//                    Log.info(TAG, "onOkClicked() IN");
//
//                    String json = readBackupFile(data);
//                    Hash hash = new Hash();
//
//                    boolean result = BackupManager.getInstance().restoreData(json, SettingsActivity.this, hash.hashMD5(enteredText));
//                    if (result) {
//                        GoodUtils.showToast(SettingsActivity.this, R.string.result_success);
//                    } else {
//                        GoodUtils.showToast(SettingsActivity.this, R.string.result_failed);
//                    }
//
//                }
//
//                @Override
//                public void onCancelClicked() {
//                    Log.info(TAG, "onCanceledClicked() IN");
//                    GoodUtils.showToast(SettingsActivity.this, R.string.result_canceled);
//                }
//            }, title, hint);

        }

        private String readBackupFile(Intent data) {
            InputStream inputStream = null;
            try {
                inputStream = getContentResolver().openInputStream(data.getData());
            } catch (FileNotFoundException e) {
                Log.error(TAG, "readBackupFile() error: " + e);
                e.printStackTrace();
                return "";
            }

            StringBuilder content = new StringBuilder();

            try {

                byte[] buffer = new byte[inputStream.available()];

                while (inputStream.read(buffer) != -1) {
                    content.append(new String(buffer));
                }

            } catch (Exception e) {
                Log.error(TAG, "readBackupFile() exception while reading the file: " + e);
                e.printStackTrace();
                return "";
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return content.toString();
        }

}
