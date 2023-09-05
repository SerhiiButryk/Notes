/*
 * Copyright 2023. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.serhii.apps.notes.R
import com.serhii.apps.notes.activities.NavigationCallback
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
class NoteViewFragment : BaseFragment(), NavigationCallback {

    private lateinit var actionButton: FloatingActionButton
    private lateinit var notesRecyclerView: RecyclerView
    private lateinit var notesViewModel: NotesViewModel
    private lateinit var adapter: NoteListAdapter
    private lateinit var interaction: NoteInteraction
    private lateinit var toolbar: Toolbar
    private lateinit var hintNotesImage: ImageView
    private lateinit var hintNotesText: TextView
    private val mainHandler = Handler(Looper.getMainLooper())
    private var isSearchActivate = false

    private val observerDataChanged = Observer<List<NoteModel>> { newNotes ->
        info(TAG, "onChanged() new data received, size = ${newNotes.size}")

        // In case it has searchable results and SearchView is not active we ignore this
        if (newNotes.isNotEmpty() && !isSearchActivate) {
            newNotes.forEach {
                if (it.queryInfo != null) {
                    info(TAG, "onChanged() has search info, ignore this as SearchView is not active")
                    return@Observer
                }
            }
        }

        info(TAG, "onChanged() going to update UI")

        mainHandler.post {
            updateUI(newNotes)
        }
    }

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

        notesViewModel = ViewModelProvider(requireActivity(), NotesViewModelFactory(requireActivity().application))
                .get(NotesViewModel::class.java)

        notesViewModel.getNotes().observe(requireActivity(), observerDataChanged)

        notesViewModel.displayMode.observe(requireActivity()) { mode ->
            if (mode == DISPLAY_MODE_LIST) {
                notesRecyclerView.layoutManager = LinearLayoutManager(context)
            } else if (mode == DISPLAY_MODE_GRID) {
                notesRecyclerView.layoutManager = GridLayoutManager(context, NOTES_COLUMN_COUNT)
            }
            saveNoteDisplayMode(requireContext(), mode)
        }

        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
    }

    override fun onNavigateBack() {
        // No-op
    }

    private fun updateUI(data: List<NoteModel>?) {
        if (data != null) {
            configureIconAndTextHint(data)
            adapter.setDataChanged(data)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        SCREEN_ORIENTATION_PREVIOUS = resources.configuration.orientation
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
                if (notesViewModel.displayMode.value == DISPLAY_MODE_GRID) {
                    notesViewModel.setDisplayNoteMode(DISPLAY_MODE_LIST)
                } else {
                    notesViewModel.setDisplayNoteMode(DISPLAY_MODE_GRID)
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
        val mode = notesViewModel.displayMode.value!!
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
        // Track SearchView state
        isSearchActivate = true
        // Do a search
        notesViewModel.performSearch(requireContext(), query)
    }

    override fun onSearchFinished() {
        // Track SearchView state
        isSearchActivate = false
        // Need to postpone a bit when SearchView state is changed (it is collapsed)
        mainHandler.post {
            notesViewModel.updateData(requireContext())
        }
    }

    private fun configureIconAndTextHint(notes: List<NoteModel>) {
        if (isSearchActivate && notes.isEmpty()) {
            // At this point we should show "Searched text is not found" so related image and text
            setVisibilityForHintImageAndText(View.VISIBLE)

            hintNotesImage.setImageDrawable(requireContext().getDrawable(R.drawable.ic_search))
            hintNotesText.text = requireContext().getText(R.string.ms_no_search_results)
        } else if (notes.isNotEmpty()) {
            // Hide "no notes" image and text
            setVisibilityForHintImageAndText(View.GONE)
        } else {
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
        private var SCREEN_ORIENTATION_PREVIOUS: Int = 1000
    }
}