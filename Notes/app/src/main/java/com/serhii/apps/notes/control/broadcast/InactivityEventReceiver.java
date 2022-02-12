package com.serhii.apps.notes.control.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.serhii.core.log.Log;
import com.serhii.apps.notes.activities.AuthorizationActivity;

public class InactivityEventReceiver extends BroadcastReceiver {

    private static final String TAG = "InactivityEventReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.detail(TAG, "Received inactivity event, TIME: " + System.currentTimeMillis());

        // Start login activity
        Intent startActivityIntent = new Intent(context, AuthorizationActivity.class);
        startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        context.startActivity(startActivityIntent);
    }

}
