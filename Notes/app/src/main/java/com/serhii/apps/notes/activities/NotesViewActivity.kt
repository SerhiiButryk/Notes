/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.serhii.apps.notes.R
import com.serhii.apps.notes.common.App
import com.serhii.apps.notes.control.auth.base.IAuthorizeUser
import com.serhii.apps.notes.control.backup.BackupManager
import com.serhii.apps.notes.ui.MenuOptions
import com.serhii.apps.notes.ui.NotesEditorUI
import com.serhii.apps.notes.ui.NotesPreviewUI
import com.serhii.apps.notes.ui.state_holders.NotesViewModel
import com.serhii.apps.notes.ui.theme.AppMaterialTheme
import com.serhii.core.log.Log

/**
 * Activity which displays user note list
 */
class NotesViewActivity : AppBaseActivity(), IAuthorizeUser {

    private val viewModel: NotesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setLoggingTagForActivity(TAG)
        super.onCreate(savedInstanceState)
        Log.info(TAG, "onCreate()")

        if (savedInstanceState == null) {
            viewModel.initViewModel(this)
        }

        setupUI()

        // Need to call initNativeConfigs() before authorization process at the beginning.
        initNativeConfigs()

        // Start authorization process.
        authorizeUser(savedInstanceState)

        // Handle back button clicks
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!viewModel.navigateBack()) {
                    moveTaskToBack(false)
                }
            }
        })
    }

    override fun onDestroy() {
        Log.info(TAG, "onDestroy()")
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (BackupManager.REQUEST_CODE_EXTRACT_NOTES == requestCode && resultCode == RESULT_OK) {
            Log.info(TAG, "onActivityResult() got result for REQUEST_CODE_EXTRACT_NOTES")
            if (data != null) {
                viewModel.extractNote(applicationContext, data)
            } else {
                // Should not happen
                Log.error(TAG, "onActivityResult() data is null")
            }
        }
    }

    /**
     * Called when the user is authorized
     */
    override fun onUserAuthorized() {
        Log.info(TAG, "onUserAuthorized()")
        viewModel.reloadData()
    }

    private fun authorizeUser(bundle: Bundle?) {
        /**
         * Activity is launched first time
         */
        if (bundle == null) {
            /*
                Start authorization activity
            */
            val intent = Intent(this, AuthorizationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupUI() {
        // Add menu options
        val menuOptionsPreview = mutableListOf<MenuOptions>()
        // Add 'Go to settings' option
        menuOptionsPreview.add(
            MenuOptions(textId = R.string.settings_item, icon = Icons.Default.Settings, onClick = {
                viewModel.openSettings(this)
            })
        )

        val menuOptionsEditor = mutableListOf<MenuOptions>()
        // Save
        menuOptionsEditor.add(
            MenuOptions(textId = R.string.save_note_item, icon = Icons.Default.Save, onClick = {
                viewModel.saveNote(viewModel.uiState.value as NotesViewModel.NotesEditorUIState, applicationContext)
            })
        )
        // Delete
        menuOptionsEditor.add(
            MenuOptions(textId = R.string.delete_note_item, icon = Icons.Default.Delete, onClick = {
                viewModel.deleteNote(viewModel.uiState.value as NotesViewModel.NotesEditorUIState)
            })
        )
        // Extract
        menuOptionsEditor.add(
            MenuOptions(textId = R.string.save_note_in_file,
                icon = Icons.Default.Backup,
                onClick = {
                    viewModel.backupNote(viewModel.uiState.value as NotesViewModel.NotesEditorUIState)
                })
        )

        setContent {
            AppMaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Box(Modifier.safeDrawingPadding()) {

                        val uiState = viewModel.uiState.collectAsStateWithLifecycle()
                        val appUiState: NotesViewModel.BaseUIState = uiState.value

                        if (appUiState is NotesViewModel.NotesMainUIState) {
                            NotesPreviewUI(appUiState, viewModel, menuOptionsPreview)
                        } else if (appUiState is NotesViewModel.NotesEditorUIState) {
                            NotesEditorUI(
                                viewModel = viewModel,
                                menuOptionsList = menuOptionsEditor,
                                uiState = appUiState
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Native interface
     *
     */
    private external fun initNativeConfigs()

    companion object {
        private const val TAG = "NotesViewActivity"
        init {
            System.loadLibrary(App.RUNTIME_LIBRARY)
        }
    }
}