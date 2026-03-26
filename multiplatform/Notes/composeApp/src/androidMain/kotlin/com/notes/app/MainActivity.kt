package com.notes.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import api.Platform
import com.notes.app.ui.EntryScreen
import api.data.loadUserData
import api.data.verifyReceived
import com.notes.ui.AppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Do not allow screenshots, screen recording etc...
        window?.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        // Load User data and then show UI
        // Ideally should show some progress here
        // TODO: Revisit to improve
        lifecycleScope.launch {
            loadUserData()
            // Handle intent if any
            handleIntent(intent)
            setContent {
                AppTheme {
                    EntryScreen()
                }
            }
        }

        Platform().logger.logi("MainActivity::onCreate() created")
    }

    private fun handleIntent(intent: Intent?) {

        val appLinkAction: String? = intent?.action
        val appLinkData: Uri? = intent?.data

        if (Intent.ACTION_VIEW == appLinkAction && appLinkData != null) {

            Platform().logger.logi("MainActivity::handleIntent() intent received")

            val oobCode = appLinkData.getQueryParameter("oobCode")
            if (oobCode != null) {
                Platform().logger.logi("MainActivity::handleIntent() " +
                        "looks like we got email verification response")
                verifyReceived(oobCode)
            }
        }
    }
}
