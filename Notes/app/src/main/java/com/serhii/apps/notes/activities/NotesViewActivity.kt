/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.serhii.apps.notes.R
import com.serhii.apps.notes.common.AppDetails
import com.serhii.apps.notes.control.NativeBridge
import com.serhii.apps.notes.control.auth.base.IAuthorizeUser
import com.serhii.apps.notes.control.auth.BiometricAuthManager
import com.serhii.apps.notes.control.background_work.BackgroundWorkHandler
import com.serhii.apps.notes.control.background_work.WorkId
import com.serhii.apps.notes.control.background_work.WorkItem
import com.serhii.apps.notes.control.backup.BackupManager
import com.serhii.apps.notes.database.UserNotesDatabase
import com.serhii.apps.notes.ui.data_model.NoteModel
import com.serhii.apps.notes.ui.fragments.NoteEditorFragment
import com.serhii.apps.notes.ui.fragments.NoteEditorFragment.EditorNoteInteraction
import com.serhii.apps.notes.ui.fragments.NoteViewFragment
import com.serhii.apps.notes.ui.fragments.NoteViewFragment.NoteInteraction
import com.serhii.apps.notes.ui.view_model.NotesViewModel
import com.serhii.core.log.Log
import com.serhii.core.log.Log.Companion.info
import com.serhii.core.utils.GoodUtils.Companion.getFilePath
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.io.OutputStream

/**
 * Activity which displays user note list
 */
class NotesViewActivity : AppBaseActivity(), IAuthorizeUser, NoteInteraction, EditorNoteInteraction {

    private val notesViewModel: NotesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setLoggingTagForActivity(TAG)

        info(TAG, "onCreate() IN")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_view)

        // Need to call initNativeConfigs() before authorization process at the beginning.
        val filePath = getFilePath(this)
        initNativeConfigs(filePath)

        if (savedInstanceState == null) {
            addNoteViewFragment()
        }

        // Start authorization process.
        authorizeUser(savedInstanceState)

        info(TAG, "onCreate() OUT")
    }

    private fun addNoteViewFragment() {
        val fm = supportFragmentManager
        val f = NoteViewFragment()
        fm.beginTransaction().replace(R.id.main_layout, f, NoteViewFragment.FRAGMENT_TAG)
            .setReorderingAllowed(true) // Needed for optimization
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        info(TAG, "onDestroy() OUT")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
// Stopped responding to this changes
//        if (BiometricAuthManager.isUnlockActivityResult(requestCode, resultCode)) {
//            info(TAG, "onActivityResult(), reload data")
//            notesViewModel.resetErrorState()
//            notesViewModel.updateData(this)
//        }

        if (BackupManager.REQUEST_CODE_EXTRACT_NOTES == requestCode && resultCode == RESULT_OK) {
            info(TAG, "onActivityResult() got result for REQUEST_CODE_EXTRACT_NOTES")
            if (data != null) {

                val noteId = getNoteId()
                if (noteId.isNullOrEmpty()) {
                    info(TAG, "onActivityResult() empty noteId, return")
                    return
                }

                val outputStream: OutputStream? = try {
                    contentResolver.openOutputStream(data.data!!)
                } catch (e: FileNotFoundException) {
                    Log.error(TAG, "onActivityResult() failed to get output stream, error: $e")
                    e.printStackTrace()
                    return
                }

                // Start an extract
                lifecycleScope.launch(NotesViewModel.defaultDispatcher) {
                    val note = UserNotesDatabase.getRecord(noteId, baseContext)
                    BackupManager.extractNotes(baseContext, outputStream, listOf(note))
                }
            } else {
                // Should not happen
                Log.error(TAG, "onActivityResult() data is null")
            }
        }
    }

    private fun getNoteId(): String? {
        val fm = supportFragmentManager
        val f = fm.findFragmentByTag(NoteEditorFragment.FRAGMENT_TAG) as? NoteEditorFragment
        if (f != null) {
            return f.getNoteId()
        }
        return ""
    }

    /**
     * Called when user is authorized
     */
    override fun onUserAuthorized() {
        info(TAG, "onAuthorize(), user is authorized")

        val nativeBridge = NativeBridge()
        nativeBridge.resetLoginLimitLeft(this)

// Stopped listening to error state updates
//        notesViewModel?.errorStateData?.observe(this) { cryptoError ->
//            if (cryptoError === CryptoError.USER_NOT_AUTHORIZED) {
//                // Request KeyStore Unlock
//                info(TAG, "onChanged() request keystore unlock")
//                BiometricAuthManager.requestUnlockActivity(this@NotesViewActivity)
//            }
//        }

        notesViewModel.updateData(this)
    }

    override fun onOpenNote(note: NoteModel?) {
        info(TAG, "onOpenNote()")

        val fm = supportFragmentManager
        if (fm.findFragmentByTag(NoteEditorFragment.FRAGMENT_TAG) != null) {
            info(TAG, "onOpenNote() fragment is already opened, return")
            return
        }

        val args = Bundle()
        if (note == null) {
            // Handle add note button click
            args.putString(NoteEditorFragment.ARG_ACTION, NoteEditorFragment.ACTION_NOTE_CREATE)
        } else {
            // Handle open note button click
            args.putString(NoteEditorFragment.ARG_NOTE_ID, note.id)
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
        // Send a notification before we move back
        notifyAboutBackNavigation()

        val fm = supportFragmentManager
        fm.popBackStack()
        // Notify View Model
        notesViewModel.onBackNavigation()
    }

    override fun onBackPressed() {
        // Send a notification before we move back
        notifyAboutBackNavigation()

        super.onBackPressed()
        // Notify View Model
        notesViewModel.onBackNavigation()
    }

    private fun notifyAboutBackNavigation() {
        val fm = supportFragmentManager
        for (f in fm.fragments) {
            if (f != null) {
                val callback = f as? NavigationCallback
                callback?.onNavigateBack()
            }
        }
    }

    // This will start auth activity
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
            System.loadLibrary(AppDetails.RUNTIME_LIBRARY)
        }
    }
}