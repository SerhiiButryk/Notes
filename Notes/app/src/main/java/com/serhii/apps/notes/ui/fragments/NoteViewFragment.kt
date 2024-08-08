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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.serhii.apps.notes.R
import com.serhii.apps.notes.activities.AppBaseActivity
import com.serhii.apps.notes.activities.SettingsActivity
import com.serhii.apps.notes.control.preferences.PreferenceManager.saveNoteDisplayModePref
import com.serhii.apps.notes.ui.data_model.NoteModel
import com.serhii.apps.notes.ui.utils.NoteListAdapter
import com.serhii.apps.notes.ui.state_holders.NotesViewModel
import com.serhii.core.log.Log

/**
 * Fragment where user sees list of notes
 */
class NoteViewFragment : BaseFragment(TAG), AppBaseActivity.NavigationCallback {

    private lateinit var actionButton: FloatingActionButton
    private lateinit var notesRecyclerView: RecyclerView
    private lateinit var adapter: NoteListAdapter
    private lateinit var interaction: NoteInteraction
    private lateinit var toolbar: Toolbar
    private lateinit var hintNotesImage: ImageView
    private lateinit var hintNotesText: TextView

    // We should specify ViewModelStoreOwner, because otherwise we get a different instance
    // of VM here. This will not be the same as we get in NotesViewActivity.
    private val viewModel: NotesViewModel by viewModels ({ requireActivity() })

    private val dataChangedObserver = Observer<NotesViewModel.NotesUIState> { uiState ->
        Log.info(TAG, "onChanged() new data has been received, size = ${uiState.currentNotes.size}")
        updateUI(uiState.currentNotes)
    }

    private val searchObserver = Observer<List<NoteModel>> { newNotes ->
        Log.info(TAG, "onChanged() new search results have been received, size = ${newNotes.size}")
        updateUI(newNotes, true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        interaction = try {
            context as NoteInteraction
        } catch (e: ClassCastException) {
            throw IllegalStateException("Should implement " + NoteInteraction::class.java.simpleName + " listener")
        }
        Log.info(TAG, "onAttach()")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        Log.info(TAG, "onCreate()")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return initView(inflater, container)
    }

    private fun initView(inflater: LayoutInflater, container: ViewGroup?): View {

        val view = inflater.inflate(R.layout.fragment_notes_view, container, false)

        // Set references
        actionButton = view.findViewById(R.id.fab)
        notesRecyclerView = view.findViewById(R.id.note_recycler_view)
        toolbar = view.findViewById(R.id.toolbar)
        hintNotesImage = view.findViewById(R.id.placeholder_imv)
        hintNotesText = view.findViewById(R.id.placeholder_txv)

        actionButton.setOnClickListener {
            interaction.onOpenNote(null)
        }

        adapter = NoteListAdapter(object : NoteListAdapter.NoteViewHolder.ClickListener {
            override fun onClick(noteModel: NoteModel?) {
                interaction.onOpenNote(noteModel)
            }
        })

        notesRecyclerView.adapter = adapter

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getUIState().observe(viewLifecycleOwner, dataChangedObserver)

        viewModel.getDisplayNoteMode().observe(viewLifecycleOwner) { mode ->
            if (mode == DISPLAY_MODE_LIST) {
                notesRecyclerView.layoutManager = LinearLayoutManager(context)
            } else if (mode == DISPLAY_MODE_GRID) {
                notesRecyclerView.layoutManager = GridLayoutManager(context, NOTES_COLUMN_COUNT)
            }
            saveNoteDisplayModePref(requireContext(), mode)
        }

        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)

        Log.info(TAG, "onActivityCreated()")
    }

    override fun onNavigateBack() {
        // No-op
    }

    private fun updateUI(data: List<NoteModel>?, isSearch: Boolean = false) {
        if (data != null) {
            updateIconAndTextHint(data, isSearch)
            adapter.updateData(data)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.note_view_menu, menu)
        updateDisplayModeIcon(menu)
        setupSearchInterface(menu)
    }

    @SuppressLint("NonConstantResourceId", "NotifyDataSetChanged")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_item -> {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            R.id.item_layout -> {
                if (viewModel.getDisplayNoteMode().value == DISPLAY_MODE_GRID) {
                    viewModel.setDisplayNoteMode(DISPLAY_MODE_LIST)
                } else {
                    viewModel.setDisplayNoteMode(DISPLAY_MODE_GRID)
                }
                updateDisplayModeIcon(item)
                return true
            }
        }
        return false
    }

    private fun updateDisplayModeIcon(menu: Menu) {
        val displayModeItem = menu.findItem(R.id.item_layout)
        updateDisplayModeIcon(displayModeItem)
    }

    private fun updateDisplayModeIcon(menuItem: MenuItem) {
        val mode = viewModel.getDisplayNoteMode().value
        if (mode == NoteModel.LIST_NOTE_VIEW_TYPE) {
            // Update icon
            menuItem.setIcon(R.drawable.ic_view_list)
            menuItem.setTitle(R.string.action_convert_to_list)
        } else if (mode == NoteModel.ONE_NOTE_VIEW_TYPE) {
            // Update icon
            menuItem.setIcon(R.drawable.ic_view_grid)
            menuItem.setTitle(R.string.action_layout_grid)
        }
    }

    override fun onSearchStarted(query: String) {
        Log.info(TAG, "onSearchStarted()")
        // Start observing data change events for this search
        viewModel.getSearchResults().removeObserver(searchObserver)
        viewModel.getSearchResults().observe(viewLifecycleOwner, searchObserver)
        // Do a search
        viewModel.performSearch(query)
    }

    override fun onSearchFinished() {
        Log.info(TAG, "onSearchFinished()")
        // Start observing data change events for this search
        viewModel.getSearchResults().removeObserver(searchObserver)
        // Update data
        viewModel.updateAllNotes()
    }

    private fun updateIconAndTextHint(notes: List<NoteModel>, isSearch: Boolean = false) {
        if (isSearch && notes.isEmpty()) {
            // This was a search and nothing was found,
            // so show "Searched text is not found" message and icon
            setVisibilityForHintImageAndText(View.VISIBLE)
            hintNotesImage.setImageDrawable(requireContext().getDrawable(R.drawable.ic_search))
            hintNotesText.text = requireContext().getText(R.string.ms_no_search_results)
        } else if (notes.isNotEmpty()) {
            // Got some new data
            // Hide "no notes" image and text
            setVisibilityForHintImageAndText(View.GONE)
        } else {
            // No data and this was not a search
            // Show "no notes" image and text
            setVisibilityForHintImageAndText(View.VISIBLE)
            hintNotesImage.setImageDrawable(requireContext().getDrawable(R.drawable.no_notes_icon))
            hintNotesText.text = requireContext().getText(R.string.ms_no_notes)
        }
    }

    private fun setVisibilityForHintImageAndText(visibility: Int) {
        hintNotesImage.visibility = visibility
        hintNotesText.visibility = visibility
    }

    interface NoteInteraction {
        fun onOpenNote(note: NoteModel?)
    }

    companion object {
        private const val TAG = "NoteViewFragment"
        const val FRAGMENT_TAG = "NoteViewFragmentTAG"
        const val DISPLAY_MODE_LIST = 1
        const val DISPLAY_MODE_GRID = 2
        private const val NOTES_COLUMN_COUNT = 2
    }
}