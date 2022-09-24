/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.activities

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.serhii.apps.notes.R
import com.serhii.apps.notes.activities.AuthorizationActivity
import com.serhii.apps.notes.common.AppConstants
import com.serhii.apps.notes.control.NativeBridge
import com.serhii.apps.notes.control.base.IAuthorizeUser
import com.serhii.apps.notes.control.managers.BackupManager
import com.serhii.apps.notes.control.managers.BiometricAuthManager
import com.serhii.apps.notes.ui.data_model.NoteModel
import com.serhii.apps.notes.ui.fragments.NoteEditorFragment
import com.serhii.apps.notes.ui.fragments.NoteEditorFragment.EditorNoteInteraction
import com.serhii.apps.notes.ui.fragments.NoteViewFragment
import com.serhii.apps.notes.ui.fragments.NoteViewFragment.NoteInteraction
import com.serhii.apps.notes.ui.view_model.NotesViewModel
import com.serhii.apps.notes.ui.view_model.NotesViewModelFactory
import com.serhii.core.log.Log.Companion.info
import com.serhii.core.security.impl.crypto.CryptoError
import com.serhii.core.utils.GoodUtils.Companion.getFilePath

class NotesViewActivity : AppBaseActivity(), IAuthorizeUser, NoteInteraction, EditorNoteInteraction {

    private var notesViewModel: NotesViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        info(TAG, "onCreate() IN")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_view)
        // Need to call initNativeConfigs() before authorization process at the beginning.
        val filePath = getFilePath(this)
        initNativeConfigs(filePath)
        if (savedInstanceState == null) {
            addFragment()
        }
        // Start authorization process.
        authorizeUser(savedInstanceState)
        info(TAG, "onCreate() OUT")
    }

    private fun addFragment() {
        val fm = supportFragmentManager
        val f = NoteViewFragment()
        fm.beginTransaction().replace(R.id.main_layout, f, null)
            .setReorderingAllowed(true) // Needed for optimization
            .commit()
    }

    override fun onStop() {
        super.onStop()
        info(TAG, "onStop()")
    }

    override fun onDestroy() {
        info(TAG, "onDestroy() IN")
        super.onDestroy()
        BackupManager.clearNotesViewModelWeakReference()
        info(TAG, "onDestroy() OUT")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (BiometricAuthManager.isUnlockActivityResult(requestCode, resultCode)) {
            info(TAG, "onActivityResult(), reload data")
            notesViewModel?.resetErrorState()
            notesViewModel?.updateData()
        }
    }

    /**
     * Called when user is authorized
     */
    override fun onUserAuthorized() {
        info(TAG, "onAuthorize(), User is authorized")
        val nativeBridge = NativeBridge()
        nativeBridge.resetLoginLimitLeft(this)
        notesViewModel = ViewModelProvider(this, NotesViewModelFactory(application)).get(NotesViewModel::class.java)
        notesViewModel?.errorStateData?.observe(this) { cryptoError ->
            if (cryptoError === CryptoError.USER_NOT_AUTHORIZED) {
                // Request KeyStore Unlock
                info(TAG, "onChanged() request keystore unlock")
                BiometricAuthManager.requestUnlockActivity(this@NotesViewActivity)
            }
        }
        notesViewModel?.updateData()
        BackupManager.setNotesViewModelWeakReference(notesViewModel)
    }

    override fun onOpenNote(noteModel: NoteModel?) {
        info(TAG, "onOpenNote()")

        val fm = supportFragmentManager
        if (fm.findFragmentByTag(NoteEditorFragment.FRAGMENT_TAG) != null) {
            info(TAG, "onOpenNote() fragment is already opened, return")
            return
        }

        val args = Bundle()
        if (noteModel == null) {
            // Handle add note button click
            args.putString(NoteEditorFragment.ARG_NOTE_ID, NoteEditorFragment.ARG_NOTE_TEMPLATE)
            args.putString(NoteEditorFragment.ARG_ACTION, NoteEditorFragment.ACTION_NOTE_CREATE)
        } else {
            // Handle open note button click
            if (noteModel.isTemplate(this)) {
                args.putString(NoteEditorFragment.ARG_NOTE_ID, NoteEditorFragment.ARG_NOTE_TEMPLATE)
            } else {
                args.putString(NoteEditorFragment.ARG_NOTE_ID, noteModel.id)
            }
            args.putString(NoteEditorFragment.ARG_ACTION, NoteEditorFragment.ACTION_NOTE_OPEN)
        }

        fm.beginTransaction().add(R.id.main_layout, NoteEditorFragment::class.java, args,
            NoteEditorFragment.FRAGMENT_TAG)
            .setReorderingAllowed(true) // Needed for optimization
            .addToBackStack(null)
            .commit()
    }

    override fun onDeleteNote() {
        val fm = supportFragmentManager
        fm.popBackStack()
    }

    override fun onBackNavigation() {
        val fm = supportFragmentManager
        fm.popBackStack()
    }

    // This will authorization activity for user to login
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
        } else {
            onUserAuthorized()
        }
    }

    /**
     * Native interface
     *
     */
    private external fun initNativeConfigs(path: String)

    companion object {
        private const val TAG = "NotesViewActivity"
        init {
            System.loadLibrary(AppConstants.RUNTIME_LIBRARY)
        }
    }
}