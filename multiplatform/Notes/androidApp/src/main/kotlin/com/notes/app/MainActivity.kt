package com.notes.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import api.Platform
import api.data.AppSettings
import com.notes.app.ui.EntryScreen
import com.notes.ui.AppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Do not allow screenshots, screen recording etc...
        window?.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        lifecycleScope.launch {
            // First handle any intent
            handleIntent(intent)
            val firstLaunch = AppSettings.getUserEmail().isEmpty()
            setContent {
                AppTheme {
                    EntryScreen(firstLaunch = firstLaunch)
                }
            }
        }

        Platform().logger.logd("MainActivity::onCreate()")
    }

    override fun onResume() {
        super.onResume()
        Platform().logger.logd("MainActivity::onResume()")
    }

    override fun onStop() {
        super.onStop()
        Platform().logger.logd("MainActivity::onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Platform().logger.logd("MainActivity::onDestroy()")
    }

    private fun handleIntent(intent: Intent?) {
        val appLinkAction: String? = intent?.action
        val appLinkData: Uri? = intent?.data

        if (Intent.ACTION_VIEW == appLinkAction && appLinkData != null) {
            Platform().logger.logd("MainActivity::handleIntent() intent received")

            val oobCode = appLinkData.getQueryParameter("oobCode")
            if (oobCode != null) {
                Platform().logger.logd(
                    "MainActivity::handleIntent() " +
                        "looks like we got email verification response",
                )
                AppSettings.cacheCode(oobCode)
            }
        }
    }
}
