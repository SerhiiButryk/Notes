/*
 * Copyright 2023. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.serhii.apps.notes.R
import com.serhii.apps.notes.activities.AppBaseActivity
import com.serhii.apps.notes.control.backup.BackupManager
import com.serhii.apps.notes.ui.data_model.NoteModel
import com.serhii.apps.notes.ui.dialogs.ConfirmDialogCallback
import com.serhii.apps.notes.ui.dialogs.DialogHelper.showConfirmDialog
import com.serhii.apps.notes.ui.utils.NoteEditorAdapter
import com.serhii.apps.notes.ui.utils.TextChangeNotifier
import com.serhii.apps.notes.ui.view_model.NotesViewModel
import com.serhii.core.log.Log
import com.serhii.core.log.Log.Companion.info
import com.serhii.core.utils.GoodUtils
import com.serhii.core.utils.GoodUtils.Companion.getText
import com.serhii.core.utils.GoodUtils.Companion.showToast

/**
 * Fragment where user enters notes
 */
class NoteEditorFragment : BaseFragment(TAG), AppBaseActivity.NavigationCallback {

    private lateinit var titleNoteField: EditText
    private lateinit var noteTimeFiled: TextView
    private lateinit var toolbar: Toolbar
    private lateinit var interaction: EditorNoteInteraction
    private var action: String? = null
    private var noteId: String? = null
    private val noteEditorAdapter = NoteEditorAdapter()

    fun getNoteId() = noteId

    /**
     * Dialog callback
     */
    private val clearDialogCallback = object : ConfirmDialogCallback {

        override fun onOk() {

            val noteModelList: List<NoteModel> = noteEditorAdapter.currentDisplayedNotes
            var isEmpty = true

            for (noteModel in noteModelList) {
                if (noteModel.viewType == NoteModel.LIST_NOTE_VIEW_TYPE
                    || noteModel.viewType == NoteModel.ONE_NOTE_VIEW_TYPE &&
                    !noteModel.isEmpty
                ) {
                    isEmpty = false
                    break
                }
            }

            if (isEmpty) {
                showToast(
                    requireActivity(),
                    R.string.toast_action_error_note_nothing_to_clear
                )
                return
            }

            for (n in noteModelList) {
                n.clearNotes()
            }

            noteEditorAdapter.setDataChanged(noteModelList.toMutableList())
        }

        override fun onCancel() {
            // no-op
        }
    }

    /**
     * Search results observer callback
     */
    private val searchObserver = Observer<List<NoteModel>> { note ->
        info(TAG, "onChanged() got search result")
        // Check result and set selection if available
        // We expect list with one element here
        if (note.size == 1) {
            // Reset text in case it had spannable text
            titleNoteField.setText(note[0].title)
            // Set selection for the title
            if (note[0].queryInfo != null) {
                GoodUtils.setTextHighlighting(note[0].queryInfo!!.rangeForNoteTitle, titleNoteField, note[0].title)
            }
            // Set selection for other notes
            noteEditorAdapter.setNoteData(note[0])
        } else {
            Log.error(TAG, "onChanged() got wrong result")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        interaction = try {
            context as EditorNoteInteraction
        } catch (e: ClassCastException) {
            throw IllegalStateException("Should implement " + EditorNoteInteraction::class.java.simpleName + " listener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return initView(inflater, container)
    }

    private fun initView(inflater: LayoutInflater, container: ViewGroup?): View {
        val view = inflater.inflate(R.layout.fragment_note_editor_view, container, false)
        // Set references
        titleNoteField = view.findViewById(R.id.title_note)
        toolbar = view.findViewById(R.id.toolbar)
        noteTimeFiled = view.findViewById(R.id.date_time_view)

        titleNoteField.addTextChangedListener(TextChangeNotifier(requireContext()))

        val noteList = view.findViewById<RecyclerView>(R.id.note_list)
        noteList.layoutManager = LinearLayoutManager(context)
        noteList.adapter = noteEditorAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            // Hide keyboard if it's open
            GoodUtils.hideKeyboard(requireContext(), it)
            interaction.onBackNavigation()
        }

        if ((activity as AppCompatActivity?)!!.supportActionBar != null) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            (activity as AppCompatActivity?)!!.supportActionBar!!.setTitle(R.string.title_toolbar)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        action = requireArguments().getString(ARG_ACTION)
        noteId = requireArguments().getString(ARG_NOTE_ID)

        processArgs()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // We should specify ViewModelStoreOwner, because otherwise we get a different instance
        // of VM here. This will not be the same as we get in NotesViewActivity.
        val viewModel: NotesViewModel by viewModels ({ requireActivity() })
        viewModel.cacheUserNote(noteEditorAdapter.getNoteList())
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        // We should specify ViewModelStoreOwner, because otherwise we get a different instance
        // of VM here. This will not be the same as we get in NotesViewActivity.
        val viewModel: NotesViewModel by viewModels ({ requireActivity() })
        val cachedList = viewModel.cachedUserNotes
        if (cachedList != null) {
            noteEditorAdapter.setDataChanged(cachedList.toMutableList())
        }
    }

    override fun onNavigateBack() {
        collapseSearchbar()
    }

    private fun processArgs() {
        if (action == ACTION_NOTE_CREATE) {
            // Display empty note
            noteEditorAdapter.prepareEmptyNote()
            // Open keyboard
            if (titleNoteField.requestFocus()) {
                GoodUtils.showKeyboard(requireContext(), titleNoteField)
            }
        } else if (action == ACTION_NOTE_OPEN) {
            // We should specify ViewModelStoreOwner, because otherwise we get a different instance
            // of VM here. This will not be the same as we get in NotesViewActivity.
            val viewModel: NotesViewModel by viewModels ({ requireActivity() })

            val note = viewModel.getNote(noteId!!)

            // Set note title
            titleNoteField.setText(note.title)

            // Set new data to list adapter. After that recycle view will be updated with
            // new data from list.
            noteEditorAdapter.setNoteData(note)
            val timeDate = note.time

            if (timeDate.isNotEmpty()) {
                val timeDateString = getString(R.string.time_date_label)
                noteTimeFiled.text = String.format(timeDateString, timeDate)
                noteTimeFiled.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Remove all items in the menu
        menu.clear()
        inflater.inflate(R.menu.editor_note_menu, menu)
        setupSearchInterface(menu)
    }

    override fun onSearchStarted(query: String) {
        Log.info(TAG, "onSearchStarted()")
        if (noteId == null) {
            Log.error(TAG, "onSearchStarted() noteId == null")
            return
        }

        // We should specify ViewModelStoreOwner, because otherwise we get a different instance
        // of VM here. This will not be the same as we get in NotesViewActivity.
        val viewModel: NotesViewModel by viewModels ({ requireActivity() })

        // Start observing data change events for this search
        viewModel.getSearchResults().removeObserver(searchObserver)
        viewModel.getSearchResults().observe(viewLifecycleOwner, searchObserver)

        val note = viewModel.getNote(noteId!!)
        viewModel.performSearch(query, note)
    }

    override fun onSearchFinished() {
        Log.info(TAG, "onSearchFinished()")
        if (noteId == null) {
            Log.error(TAG, "onSearchFinished() unexpected error, noteId == null")
            return
        }

        // We should specify ViewModelStoreOwner, because otherwise we get a different instance
        // of VM here. This will not be the same as we get in NotesViewActivity.
        val viewModel: NotesViewModel by viewModels ({ requireActivity() })

        // We should reset selection on NotesViewFragment when SearchView is closed
        val note = viewModel.getNote(noteId!!)
        titleNoteField.setText(note.title)
        noteEditorAdapter.setNoteData(note)

        // Remove observer for this search
        viewModel.getSearchResults().removeObserver(searchObserver)
    }

    @SuppressLint("NonConstantResourceId") // Suppress "Checks use of resource IDs in places requiring constants." warning
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_note_item -> {
                /**
                 * Confirm dialog before delete
                 */
                showConfirmDialog(
                    requireActivity(),
                    object : ConfirmDialogCallback {
                        override fun onOk() {
                            deleteNote()
                        }

                        override fun onCancel() {
                            // no-op
                        }
                    },
                    R.string.confirm_dialog_delete_note_title,
                    R.string.confirm_dialog_delete_note_message
                )
                return true
            }
            R.id.save_note -> {
                /**
                 * Confirm dialog before save
                 */
                showConfirmDialog(
                    requireActivity(),
                    object : ConfirmDialogCallback {
                        override fun onOk() {
                            saveUserNote()
                        }

                        override fun onCancel() {
                            // no-op
                        }
                    },
                    R.string.confirm_dialog_save_note_title,
                    R.string.confirm_dialog_save_note_message
                )
                return true
            }
            R.id.clear_note -> {
                /**
                 * Clears Edit Text Views
                 */
                showConfirmDialog(requireActivity(), clearDialogCallback, R.string.confirm_dialog_clear_note_title, R.string.confirm_dialog_clear_note_message)
                return true
            }
            R.id.item_type -> {
                /**
                 * Change note layout
                 */
                noteEditorAdapter.transformView()
                return true
            }
            R.id.save_note_in_file -> {
                /**
                 * Save a note to a file
                 */
                BackupManager.openDirectoryChooserForExtractData(requireActivity())
                return true
            }
        }
        return false
    }

    private fun saveUserNote() {
        val notes = noteEditorAdapter.getNote()
        notes.title = getText(titleNoteField)

        if (notes.isEmpty) {
            showToast(requireContext(), R.string.toast_action_error_note_is_empty)
            return
        }

        // We should specify ViewModelStoreOwner, because otherwise we get a different instance
        // of VM here. This will not be the same as we get in NotesViewActivity.
        val viewModel: NotesViewModel by viewModels ({ requireActivity() })

        val result: Boolean = if (action == ACTION_NOTE_OPEN) {
            info(TAG, "saveUserNote() updated note")
            viewModel.updateNote(noteId!!, notes)
        } else {
            info(TAG, "saveUserNote() add new note")
            viewModel.addNote(notes)
        }

        /*
            Display info toast message
        */
        if (result) {
            info(TAG, "saveUserNote() saved new note")
            showToast(requireContext(), R.string.toast_action_message)
        }
    }

    private fun deleteNote() {
        if (noteEditorAdapter.getNote().isEmpty) {
            showToast(requireContext(), R.string.toast_action_delete_error_note)
            return
        }

        // We should specify ViewModelStoreOwner, because otherwise we get a different instance
        // of VM here. This will not be the same as we get in NotesViewActivity.
        val viewModel: NotesViewModel by viewModels ({ requireActivity() })

        // Delete if this note already exists
        if (noteId != null && viewModel.deleteNote(noteId!!)) {
            showToast(requireContext(), R.string.toast_action_deleted_message)
            interaction.onDeleteNote()
        } else {
            // Show warning message otherwise
            showToast(requireContext(), R.string.toast_action_deleted_message_no_note)
        }
    }

    interface EditorNoteInteraction {
        fun onBackNavigation()
        fun onDeleteNote()
    }

    companion object {
        private const val TAG = "NoteEditorFragment"
        const val FRAGMENT_TAG = "NoteEditorFragmentTAG"
        const val ACTION_NOTE_OPEN = "action open note"
        const val ACTION_NOTE_CREATE = "action create note"
        const val ARG_ACTION = "action arg"
        const val ARG_NOTE_ID = "note id arg"
    }
}