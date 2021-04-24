package com.serhii.apps.notes.ui.fragments;

import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.serhii.apps.notes.R;
import com.serhii.apps.notes.common.AppConstants;
import com.serhii.apps.notes.control.NativeBridge;
import com.serhii.apps.notes.control.managers.BackupManager;
import com.serhii.apps.notes.ui.dialogs.DialogHelper;
import com.serhii.core.log.Log;
import com.serhii.core.utils.GoodUtils;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static String TAG = LoginFragment.class.getSimpleName();

    private Preference changePassword;
    private Preference loginAttempts;
    private Preference version;
    private ListPreference idleLockTimeOut;
    private Preference extractNotes;
    private Preference backupNotes;

    private final NativeBridge nativeBridge = new NativeBridge();
    private final BackupManager backupManager = new BackupManager();

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.preferences, s);
        initBasicSettings();
    }

    private void initBasicSettings() {
        changePassword = findPreference(getString(R.string.preference_change_password_key));

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

        version = findPreference(getString(R.string.preference_about_version_key));
        version.setSummary(AppConstants.VERSION_LIBRARY);

        idleLockTimeOut = findPreference(getString(R.string.preference_idle_lock_timeout_key));

        idleLockTimeOut.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int selectedTime = Integer.parseInt(newValue.toString());
                Log.info(TAG, "onPreferenceChange(), selected idle lock time " + selectedTime);
                return true;
            }
        });

        extractNotes = findPreference(getString(R.string.preference_extract_key));

        extractNotes.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                backupManager.openDirectoryChooser(getActivity());
                return true;
            }
        });

        backupNotes = findPreference(getString(R.string.preference_backup_key));

        backupNotes.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                return false;
            }
        });

    }

}
