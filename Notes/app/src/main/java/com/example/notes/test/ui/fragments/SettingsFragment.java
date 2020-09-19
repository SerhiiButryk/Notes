package com.example.notes.test.ui.fragments;

import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.core.common.log.Log;
import com.example.core.utils.GoodUtils;
import com.example.notes.test.R;
import com.example.notes.test.common.AppContants;
import com.example.notes.test.control.NativeBridge;
import com.example.notes.test.ui.dialogs.impl.ChangePasswordDialog;
import com.example.notes.test.ui.dialogs.impl.SetAttemptLimitDialog;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static String TAG = LoginFragment.class.getSimpleName();

    private Preference changePassword;
    private Preference loginAttempts;
    private Preference version;
    private ListPreference idleLockTimeOut;

    private final NativeBridge nativeBridge = new NativeBridge();

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.preferences, s);

        initBasicSettings();
    }

    public void updatePreferences() {
        int limitAttempts = nativeBridge.getAttemptLimit();

        loginAttempts.setSummary(GoodUtils.formatString(getString(R.string.preference_login_limit_summary), limitAttempts));
    }

    private void initBasicSettings() {
        changePassword = findPreference(getString(R.string.preference_change_password_key));

        changePassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ChangePasswordDialog.showDialog(SettingsFragment.this.getActivity());
                return true;
            }
        });

        loginAttempts = findPreference(getString(R.string.preference_login_limit_key));

        loginAttempts.setSummary(GoodUtils.formatString(getString(R.string.preference_login_limit_summary),
                nativeBridge.getAttemptLimit()));

        loginAttempts.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SetAttemptLimitDialog.showDialog(getActivity());
                return true;
            }
        });

        version = findPreference(getString(R.string.preference_about_version_key));
        version.setSummary(AppContants.VERSION_LIBRARY);

        idleLockTimeOut = findPreference(getString(R.string.preference_idle_lock_timeout_key));

        // Restore default value
        int index = idleLockTimeOut.findIndexOfValue(String.valueOf(nativeBridge.getIdleLockTime()));
        idleLockTimeOut.setValueIndex(index);

        idleLockTimeOut.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                int selectedTime = Integer.parseInt(newValue.toString());
                int currentTime = nativeBridge.getIdleLockTime();

                Log.info(TAG, "idleLockTimOut selectedTime " + selectedTime);
                Log.info(TAG, "idleLockTimOut currentTime " + currentTime);

                if (currentTime != selectedTime) {
                    nativeBridge.setIdleLockTime(selectedTime);
                }

                return true;
            }
        });
    }

}
