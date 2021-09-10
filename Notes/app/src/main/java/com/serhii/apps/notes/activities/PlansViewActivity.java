package com.serhii.apps.notes.activities;

import android.os.Bundle;

import com.serhii.apps.notes.R;
import com.serhii.apps.notes.ui.fragments.PlansFragment;
import com.serhii.core.log.Log;
import com.serhii.core.utils.GoodUtils;

import androidx.appcompat.app.AppCompatActivity;

public class PlansViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans_view);

        // Enable unsecured screen content settings
        Log.info("PlansViewActivity", "onCreate() is unsecured screen content enabled - " + GoodUtils.enableUnsecureScreenProtection(this));

        if (savedInstanceState == null) {
            addFragment();
        }

    }

    private void addFragment() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true) // Needed for optimization
                .add(R.id.plans_container_layout, new PlansFragment())
                .commit();
    }

}