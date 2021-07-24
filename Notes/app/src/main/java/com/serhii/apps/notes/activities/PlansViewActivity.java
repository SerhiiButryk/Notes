package com.serhii.apps.notes.activities;

import android.os.Bundle;
import android.view.WindowManager;

import com.serhii.apps.notes.R;
import com.serhii.apps.notes.ui.fragments.PlansFragment;

import androidx.appcompat.app.AppCompatActivity;

public class PlansViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans_view);

        // Enable unsecured screen content settings
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

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