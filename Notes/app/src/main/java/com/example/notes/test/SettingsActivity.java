package com.example.notes.test;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.view.View;
import android.view.WindowManager;

import com.example.notes.test.databinding.ActivitySettingsBinding;
import com.example.notes.test.ui.SetLoginLimitDialogUI;
import com.example.notes.test.ui.fragments.SettingsFragment;
import com.example.notes.test.ui.utils.UserInactivityManager;

public class SettingsActivity extends AppCompatActivity implements SetLoginLimitDialogUI.OnNewValueSet {

    private static final String SETTINGS_FRAGMENT_TAG = "settings fragment";

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable unsecured screen content settings
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        initBinding();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new SettingsFragment(), SETTINGS_FRAGMENT_TAG).commit();

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.preference_title));
        }

        // Lifecycle aware component
        UserInactivityManager.getInstance().setLifecycle(this, getLifecycle());

    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

        UserInactivityManager.getInstance().onUserInteraction();
    }

    @Override
    public void onNewValueSet() {
        SettingsFragment settingsFragment = (SettingsFragment) getSupportFragmentManager().findFragmentByTag(SETTINGS_FRAGMENT_TAG);
        settingsFragment.updatePreferences();
    }

    private void initBinding() {
        ActivitySettingsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);

        // Set references
        toolbar = binding.toolbar;
    }


}
