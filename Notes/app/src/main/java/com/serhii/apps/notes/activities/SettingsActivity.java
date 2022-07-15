package com.serhii.apps.notes.activities;

import static com.serhii.apps.notes.control.managers.BackupManager.REQUEST_CODE_BACKUP_NOTES;
import static com.serhii.apps.notes.control.managers.BackupManager.REQUEST_CODE_OPEN_BACKUP_FILE;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.serhii.apps.notes.R;
import com.serhii.apps.notes.control.broadcast.InactivityEventReceiver;
import com.serhii.apps.notes.control.managers.AppForegroundListener;
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

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private static final String SETTINGS_FRAGMENT_TAG = "main settings";

    private Toolbar toolbar;
    private final AppForegroundListener appForegroundListener = new AppForegroundListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable unsecured screen content settings
        GoodUtils.enableUnsecureScreenProtection(this);

        initBinding();

        if (savedInstanceState == null) {
            addFragment();
        }

        setActionBar();

        // Lifecycle aware component
        getLifecycle().addObserver(appForegroundListener);

    }

    @Override
    protected void onResume() {
        Log.info(TAG, "onResume()");
        super.onResume();
        InactivityEventReceiver.checkIfInactivityTimeoutReceived(this);
        // Trigger time out
        InactivityManager.getInstance().scheduleAlarm();
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
            OutputStream outputStream = null;
            try {
                outputStream = getContentResolver().openOutputStream(data.getData());
            } catch (FileNotFoundException e) {
                Log.error(TAG, "backupNotes() error: " + e);
                e.printStackTrace();
                return;
            }

            boolean result = BackupManager.getInstance().backupData(outputStream, this);
            if (result) {
                GoodUtils.showToast(this, R.string.result_success);
            } else {
                GoodUtils.showToast(this, R.string.result_failed);
            }

        }

        private void restoreNotes(Intent data) {
            InputStream inputStream = null;
            try {
                inputStream = getContentResolver().openInputStream(data.getData());
            } catch (FileNotFoundException e) {
                Log.error(TAG, "restoreNotes() error: " + e);
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
                Log.error(TAG, "restoreNotes() exception while reading the file: " + e);
                e.printStackTrace();
                return;
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            boolean result = BackupManager.getInstance().restoreData(content.toString(), this);
            if (result) {
                GoodUtils.showToast(this, R.string.result_success);
            } else {
                GoodUtils.showToast(this, R.string.result_failed);
            }
        }

}
