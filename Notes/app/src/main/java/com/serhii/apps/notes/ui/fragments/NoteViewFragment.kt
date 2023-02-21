/*
 * Copyright 2023. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.serhii.apps.notes.R
import com.serhii.apps.notes.activities.SettingsActivity
import com.serhii.apps.notes.control.preferences.PreferenceManager.saveNoteDisplayMode
import com.serhii.apps.notes.ui.data_model.NoteModel
import com.serhii.apps.notes.ui.utils.NoteListAdapter
import com.serhii.apps.notes.ui.view_model.NotesViewModel
import com.serhii.apps.notes.ui.view_model.NotesViewModelFactory
import com.serhii.core.log.Log.Companion.info

/**
 * Fragment where user sees list of notes
 */
class NoteViewFragment : Fragment() {

    private var actionButton: FloatingActionButton? = null
    private var notesRecyclerView: RecyclerView? = null
    private var notesViewModel: NotesViewModel? = null
    private var adapter: NoteListAdapter? = null
    private var interaction: NoteInteraction? = null
    private var toolbar: Toolbar? = null
    private var noNotesImage: ImageView? = null
    private var noNotesText: TextView? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        interaction = try {
            context as NoteInteraction
        } catch (e: ClassCastException) {
            throw IllegalStateException("Should implement " + NoteInteraction::class.java.simpleName + " listener")
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

        val v = initView(inflater, container)
        actionButton!!.setOnClickListener {
            if (interaction != null) {
                interaction!!.onOpenNote(null)
            }
        }

        adapter = NoteListAdapter(object : NoteListAdapter.NoteViewHolder.ClickListener {
            override fun onClick(noteModel: NoteModel?) {
                if (interaction != null) {
                    interaction!!.onOpenNote(noteModel)
                }
            }
        })

        notesRecyclerView!!.adapter = adapter
        return v
    }

    fun initView(inflater: LayoutInflater, container: ViewGroup?): View {

        val view = inflater.inflate(R.layout.fragment_notes_view, container, false)

        // Set references
        actionButton = view.findViewById(R.id.fab)
        notesRecyclerView = view.findViewById(R.id.note_recycler_view)
        toolbar = view.findViewById(R.id.toolbar)
        noNotesImage = view.findViewById(R.id.placeholder_imv)
        noNotesText = view.findViewById(R.id.placeholder_txv)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        notesViewModel =
            ViewModelProvider(requireActivity(), NotesViewModelFactory(requireActivity().application))
                .get(NotesViewModel::class.java)

        notesViewModel!!.getNotes().observe(requireActivity()) { noteModels ->
            info(TAG, "onChanged() new data received, size = " + noteModels.size)
            if (!noteModels.isEmpty()) {
                // Hide "no notes" image and text
                noNotesImage!!.visibility = View.GONE
                noNotesText!!.visibility = View.GONE
            } else {
                // Show "no notes" image and text
                noNotesImage!!.visibility = View.VISIBLE
                noNotesText!!.visibility = View.VISIBLE
            }
            adapter!!.setDataChanged(notesViewModel!!.getNotes().value!!)
        }

        notesViewModel!!.displayMode.observe(requireActivity()) { mode ->
            if (mode == DISPLAY_MODE_LIST) {
                notesRecyclerView!!.layoutManager = LinearLayoutManager(context)
            } else if (mode == DISPLAY_MODE_GRID) {
                notesRecyclerView!!.layoutManager = GridLayoutManager(context, NOTES_COLUMN_COUNT)
            }
            saveNoteDisplayMode(requireContext(), mode)
        }

        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.note_view_menu, menu)
        val displayModeItem = menu.findItem(R.id.item_layout)
        updateDisplayNoteViewIcon(displayModeItem)
    }

    @SuppressLint("NonConstantResourceId", "NotifyDataSetChanged")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_item -> {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            R.id.item_layout -> {
                if (notesViewModel!!.displayMode.value == DISPLAY_MODE_GRID) {
                    notesViewModel!!.setDisplayNoteMode(DISPLAY_MODE_LIST)
                } else {
                    notesViewModel!!.setDisplayNoteMode(DISPLAY_MODE_GRID)
                }
                updateDisplayNoteViewIcon(item)
                return true
            }
        }
        return false
    }

    private fun updateDisplayNoteViewIcon(displayModeItem: MenuItem) {
        val mode = notesViewModel!!.displayMode.value!!
        if (mode == NoteModel.LIST_NOTE_VIEW_TYPE) {
            // Update icon
            displayModeItem.setIcon(R.drawable.ic_view_list)
            displayModeItem.setTitle(R.string.action_convert_to_list)
        } else if (mode == NoteModel.ONE_NOTE_VIEW_TYPE) {
            // Update icon
            displayModeItem.setIcon(R.drawable.ic_view_grid)
            displayModeItem.setTitle(R.string.action_layout_grid)
        }
    }

    interface NoteInteraction {
        fun onOpenNote(note: NoteModel?)
    }

    companion object {
        private const val TAG = "NoteViewFragment"
        const val DISPLAY_MODE_LIST = 1
        const val DISPLAY_MODE_GRID = 2
        private const val NOTES_COLUMN_COUNT = 2
    }
}