package com.serhii.apps.notes.ui.fragments;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.serhii.apps.notes.R;
import com.serhii.apps.notes.common.AppConstants;
import com.serhii.apps.notes.control.NativeBridge;
import com.serhii.apps.notes.control.managers.BackupManager;
import com.serhii.apps.notes.control.idle_lock.InactivityManager;
import com.serhii.apps.notes.database.NotesDatabaseProvider;
import com.serhii.apps.notes.ui.dialogs.DialogHelper;
import com.serhii.core.log.Log;
import com.serhii.core.utils.GoodUtils;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final String TAG = "LoginFragment";

    private Preference loginAttempts;

    private final NativeBridge nativeBridge = new NativeBridge();

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.preferences, s);
        initLayout();
    }

    private void initLayout() {
        Preference changePassword = findPreference(getString(R.string.preference_change_password_key));

        changePassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                DialogHelper.showChangePasswordDialog(getActivity());
                return true;
            }
        });

        loginAttempts = findPreference(getString(R.string.preference_login_limit_key));
        loginAttempts.setSummary(GoodUtils.formatString(getString(R.string.preference_login_limit_summary),
                nativeBridge.getLockLimit(getContext())));

        loginAttempts.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int selectedValue = Integer.parseInt(newValue.toString());
                Log.info(TAG, "onPreferenceChange(), selected new login limit: " + selectedValue);

                loginAttempts.setSummary(GoodUtils.formatString(getString(R.string.preference_login_limit_summary), selectedValue));

                nativeBridge.setLimitLeft(selectedValue);

                return true;
            }
        });

        Preference version = findPreference(getString(R.string.preference_about_version_key));
        version.setSummary(AppConstants.VERSION_LIBRARY);

        Preference idleLockTimeOut = findPreference(getString(R.string.preference_idle_lock_timeout_key));

        idleLockTimeOut.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int selectedTime = Integer.parseInt(newValue.toString());
                Log.info(TAG, "onPreferenceChange(), selected idle lock time " + selectedTime);
                InactivityManager.updateTimeout(getContext());
                return true;
            }
        });

        Preference extractNotes = findPreference(getString(R.string.preference_extract_key));

        extractNotes.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                Log.info(TAG, "onPreferenceClick()");

                if (!isDataAvailable()) {
                    return true;
                }

                BackupManager.getInstance().openDirectoryChooserForExtractData(getActivity());
                return true;
            }
        });

        Preference backupNotes = findPreference(getString(R.string.preference_backup_key));

        backupNotes.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                if (!isDataAvailable()) {
                    return true;
                }

                BackupManager.getInstance().openDirectoryChooserForBackup(getActivity());

                return false;
            }
        });

        Preference unlockNote = findPreference(getString(R.string.preference_unlock_note_key));
        unlockNote.setSummary(nativeBridge.getUnlockKey());

        Preference restoreNotes = findPreference(getString(R.string.preference_restore_note_key));
        restoreNotes.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                BackupManager.getInstance().openBackUpFile(getActivity());
                return true;
            }
        });

    }

    private boolean isDataAvailable() {
        NotesDatabaseProvider notesDatabaseProvider = new NotesDatabaseProvider(getActivity());

        // Check if there is data to extract
        if (notesDatabaseProvider.getRecordsCount() == 0) {
            DialogHelper.showAlertDialog(DialogHelper.ALERT_DIALOG_TYPE_BACKUP_ERROR, getActivity());
            Log.error(TAG, "isDataAvailable() no data available");
            return false;
        }

        return true;
    }

}
