package com.example.notes.test.ui.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.core.common.log.Log;
import com.example.notes.test.AuthorizationActivity;

public class InactivityEventReceiver extends BroadcastReceiver {

    private static final String TAG = "InactivityEventReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.info(TAG, "received inactivity event TIME: " + System.currentTimeMillis());

        // Start login activity
        Intent startActivityIntent = new Intent(context, AuthorizationActivity.class);
        startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        context.startActivity(startActivityIntent);
    }

}
