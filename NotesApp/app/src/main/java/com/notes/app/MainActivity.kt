package com.notes.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.notes.app.ui.EntryScreen
import com.notes.auth_ui.data.loadUserData
import com.notes.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Load User data and then show UI
        // Ideally should show some progress here
        // TODO: Revisit to improve
        lifecycleScope.launch {
            loadUserData()
            setContent {
                AppTheme {
                    EntryScreen()
                }
            }
        }
    }

}