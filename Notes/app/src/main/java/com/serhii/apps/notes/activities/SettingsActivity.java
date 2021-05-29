package com.serhii.apps.notes.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.serhii.apps.notes.R;
import com.serhii.apps.notes.control.managers.BackupManager;
import com.serhii.apps.notes.control.managers.InactivityManager;
import com.serhii.apps.notes.databinding.ActivitySettingsBinding;
import com.serhii.apps.notes.ui.fragments.SettingsFragment;
import com.serhii.core.log.Log;
import com.serhii.core.utils.GoodUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.serhii.apps.notes.control.managers.BackupManager.REQUEST_CODE_BACKUP_NOTES;
import static com.serhii.apps.notes.control.managers.BackupManager.REQUEST_CODE_OPEN_BACKUP_FILE;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    private static final String SETTINGS_FRAGMENT_TAG = "main settings";

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable unsecured screen content settings
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        initBinding();

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true) // Needed for optimization
                .replace(R.id.container, new SettingsFragment(), SETTINGS_FRAGMENT_TAG)
                .commit();

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.preference_title));
        }

        // Lifecycle aware component
        InactivityManager.getInstance().setLifecycle(this, getLifecycle());

    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

        InactivityManager.getInstance().onUserInteraction();
    }

    private void initBinding() {
        ActivitySettingsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);

        // Set references
        toolbar = binding.toolbar;

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
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

                OutputStream outputStream = null;
                try {
                    outputStream = getContentResolver().openOutputStream(data.getData());
                } catch (FileNotFoundException e) {
                    Log.error(TAG, "onActivityResult() error: " + e);
                    e.printStackTrace();
                    return;
                }

                BackupManager backupManager = new BackupManager();
                boolean result = backupManager.saveDataAsPlainText(outputStream, this);
                if (result) {
                    GoodUtils.showToast(this, R.string.result_success);
                } else {
                    GoodUtils.showToast(this, R.string.result_failed);
                }

            } else if (requestCode == REQUEST_CODE_BACKUP_NOTES && resultCode == RESULT_OK) {

                Log.info(TAG, "onActivityResult() got result for REQUEST_CODE_BACKUP_NOTES");

                OutputStream outputStream = null;
                try {
                    outputStream = getContentResolver().openOutputStream(data.getData());
                } catch (FileNotFoundException e) {
                    Log.error(TAG, "onActivityResult() error: " + e);
                    e.printStackTrace();
                    return;
                }

                BackupManager backupManager = new BackupManager();
                boolean result = backupManager.backupDataAsEncryptedText(outputStream, this);
                if (result) {
                    GoodUtils.showToast(this, R.string.result_success);
                } else {
                    GoodUtils.showToast(this, R.string.result_failed);
                }

                backupManager.setPassword(null);

            } else if (requestCode == REQUEST_CODE_OPEN_BACKUP_FILE && resultCode == RESULT_OK) {

                Log.info(TAG, "onActivityResult() got result for REQUEST_CODE_OPEN_BACKUP_FILE");

                InputStream inputStream = null;
                try {
                    inputStream = getContentResolver().openInputStream(data.getData());
                } catch (FileNotFoundException e) {
                    Log.error(TAG, "onActivityResult() error: " + e);
                    e.printStackTrace();
                    return;
                }

                StringBuilder content = new StringBuilder();

                try {

                    byte[] buffer = new byte[inputStream.available()];

                    while (inputStream.read(buffer) != -1) {
                        content.append(new String(buffer));
                    }

                } catch (Exception e) {
                    Log.error(TAG, "onActivityResult() exception while reading the file: " + e);
                    e.printStackTrace();
                    return;
                } finally {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                BackupManager backupManager = new BackupManager();
                boolean result = backupManager.restoreData(content.toString(), this);
                if (result) {
                    GoodUtils.showToast(this, R.string.result_success);
                } else {
                    GoodUtils.showToast(this, R.string.result_failed);
                }

            }

        }

}
