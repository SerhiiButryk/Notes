package com.example.notes.test.activities;

import android.os.Bundle;
import android.view.WindowManager;

import com.example.notes.test.R;
import com.example.notes.test.ui.fragments.PlansFragment;

import androidx.appcompat.app.AppCompatActivity;

public class PlansViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans_view);

        // Enable unsecured screen content settings
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true) // Needed for optimization
                .add(R.id.plans_container_layout, new PlansFragment())
                .commit();
    }

}