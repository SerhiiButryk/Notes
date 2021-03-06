package com.serhii.apps.notes.ui.fragments;

import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.serhii.core.log.Log;
import com.serhii.core.utils.GoodUtils;
import com.serhii.apps.notes.R;
import com.serhii.apps.notes.common.AppConstants;
import com.serhii.apps.notes.control.NativeBridge;
import com.serhii.apps.notes.ui.dialogs.DialogHelper;

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
                DialogHelper.showChangePasswordDialog(getActivity());
                return true;
            }
        });

        loginAttempts = findPreference(getString(R.string.preference_login_limit_key));

        loginAttempts.setSummary(GoodUtils.formatString(getString(R.string.preference_login_limit_summary),
                nativeBridge.getAttemptLimit()));

        loginAttempts.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                DialogHelper.showSetAttemptDialog(getActivity());
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
    }

}
