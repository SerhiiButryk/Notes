/*
 * Copyright 2023. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.serhii.apps.notes.R
import com.serhii.apps.notes.control.idle_lock.IdleLockHandler
import com.serhii.apps.notes.ui.data_model.NoteModel
import com.serhii.apps.notes.ui.dialogs.ConfirmDialogCallback
import com.serhii.apps.notes.ui.dialogs.DialogHelper.showConfirmDialog
import com.serhii.apps.notes.ui.utils.NoteEditorAdapter
import com.serhii.apps.notes.ui.utils.TextChangeNotifier
import com.serhii.apps.notes.ui.view_model.NotesViewModel
import com.serhii.apps.notes.ui.view_model.NotesViewModelFactory
import com.serhii.core.log.Log.Companion.info
import com.serhii.core.utils.GoodUtils.Companion.getText
import com.serhii.core.utils.GoodUtils.Companion.showToast

/**
 * Fragment where user enters notes
 */
class NoteEditorFragment : Fragment() {

    private var titleNoteField: EditText? = null
    private var noteTimeFiled: TextView? = null
    private var toolbar: Toolbar? = null
    private var notesViewModel: NotesViewModel? = null
    private var interaction: EditorNoteInteraction? = null
    private var action: String? = null
    private var noteId: String? = null
    private val noteEditorAdapter = NoteEditorAdapter()

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
    ): View? {
        return initView(inflater, container)
    }

    private fun initView(inflater: LayoutInflater, container: ViewGroup?): View {
        val view = inflater.inflate(R.layout.fragment_note_editor_view, container, false)
        // Set references
        titleNoteField = view.findViewById(R.id.title_note)
        toolbar = view.findViewById(R.id.toolbar)
        noteTimeFiled = view.findViewById(R.id.date_time_view)

        titleNoteField?.addTextChangedListener(TextChangeNotifier(requireContext()))

        val noteList = view.findViewById<RecyclerView>(R.id.note_list)
        noteList.layoutManager = LinearLayoutManager(context)
        noteList.adapter = noteEditorAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)

        toolbar!!.setNavigationOnClickListener {
            if (interaction != null) {
                // Hide keyboard if it's open
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(requireView().windowToken, 0)
                interaction!!.onBackNavigation()
            }
        }

        if ((activity as AppCompatActivity?)!!.supportActionBar != null) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            (activity as AppCompatActivity?)!!.supportActionBar!!.setTitle(R.string.title_toolbar)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        notesViewModel =
            ViewModelProvider(requireActivity(), NotesViewModelFactory(requireActivity().application))
                .get(NotesViewModel::class.java)

        action = requireArguments().getString(ARG_ACTION)
        noteId = requireArguments().getString(ARG_NOTE_ID)

        processArgs()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        notesViewModel!!.cacheUserNote(noteEditorAdapter.getNoteList())
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val cachedList = notesViewModel!!.cachedUserNotes
        if (cachedList != null) {
            noteEditorAdapter.setDataChanged(cachedList.toMutableList())
        }
    }

    private fun processArgs() {
        if (action == ACTION_NOTE_CREATE) {
            // Display empty note
            noteEditorAdapter.prepareEmptyNote()
        } else if (action == ACTION_NOTE_OPEN) {

            val note = notesViewModel!!.getNote(noteId!!, requireContext())

            if (note != null) {
                // Set note title
                titleNoteField!!.setText(note.title)

                // Set new data to list adapter. After that recycle view will be updated with
                // new data from list.
                noteEditorAdapter.setNoteData(note)
                val timeDate = note.time

                if (!timeDate.isEmpty()) {
                    val timeDateString = getString(R.string.time_date_label)
                    noteTimeFiled!!.text = String.format(timeDateString, timeDate)
                    noteTimeFiled!!.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Remove all items in the menu
        menu.clear()
        inflater.inflate(R.menu.editor_note_menu, menu)
    }

    @SuppressLint("NonConstantResourceId") // Suppress "Checks use of resource IDs in places requiring constants." warnning
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_note_item -> {
                /**
                 * Confirm dialog before delete
                 */
                showConfirmDialog(
                    requireActivity(),
                    object : ConfirmDialogCallback {
                        override fun onOkClicked() {
                            deleteNote()
                        }

                        override fun onCancelClicked() {
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
                        override fun onOkClicked() {
                            saveUserNote()
                        }

                        override fun onCancelClicked() {
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
                showConfirmDialog(
                    requireActivity(),
                    object : ConfirmDialogCallback {
                        override fun onOkClicked() {

                            val noteModelList: List<NoteModel> = noteEditorAdapter.getCurrentList()
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

                        override fun onCancelClicked() {
                            // no-op
                        }
                    },
                    R.string.confirm_dialog_clear_note_title,
                    R.string.confirm_dialog_clear_note_message
                )
                return true
            }
            R.id.item_type -> {
                /**
                 * Change note layout
                 */
                noteEditorAdapter.transformView()
                return true
            }
        }
        return false
    }

    private fun saveUserNote() {
        val notes = noteEditorAdapter.getNote()
        notes.title = getText(titleNoteField!!)

        if (notes.isEmpty) {
            showToast(requireContext(), R.string.toast_action_error_note_is_empty)
            return
        }

        val result: Boolean = if (action == ACTION_NOTE_OPEN) {
            info(
                TAG,
                "saveUserNote() updated note"
            )
            notesViewModel!!.updateNote(noteId!!, notes, requireContext())
        } else {
            info(
                TAG,
                "saveUserNote() add new note"
            )
            notesViewModel!!.addNote(notes, requireContext())
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

        // Delete if this note already exists
        if (noteId != null && notesViewModel!!.deleteNote(noteId!!, requireContext())) {
            showToast(requireContext(), R.string.toast_action_deleted_message)
            if (interaction != null) {
                interaction!!.onDeleteNote()
            }
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