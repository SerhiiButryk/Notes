/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.ui.utils

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.checkbox.MaterialCheckBox
import com.serhii.apps.notes.R
import com.serhii.apps.notes.ui.data_model.NoteList
import com.serhii.apps.notes.ui.data_model.NoteModel
import com.serhii.core.log.Log
import com.serhii.core.utils.GoodUtils

const val TAG = "NoteEditorAdapter"

/**
 * Adapter for managing views and note data in Editor Note Fragment
 */
class NoteEditorAdapter : RecyclerView.Adapter<NoteViewHolderBase>(), UserActionHandler {

    /**
     * This is a type of view with "add new list item" button
     */
    private val ADD_NEW_NOTE_VIEW_TYPE = 101

    var currentDisplayedNotes = mutableListOf<NoteModel>()

    /**
     * Return a copy of note data which Recycle View displays
     */
    fun getNoteList() : MutableList<NoteModel> {
        // We need to remove empty note which we creates for displaying "add new list item" button
        if (currentDisplayedNotes.isNotEmpty()) {
            val copyList = mutableListOf<NoteModel>()
            // Iterate through list and get notes
            for (note in currentDisplayedNotes) {
                if (note.viewType != ADD_NEW_NOTE_VIEW_TYPE) {
                    copyList.add(NoteModel.getCopy(note))
                }
            }
            return copyList
        }
        // Else return as it is
        return currentDisplayedNotes
    }

    /**
     * Return note data which Recycle View displays
     *
     * Unlike above method this merges list note in one Note Model object
     * and returns it
     */
    fun getNote() : NoteModel {
        val note = NoteModel()
        val list = getNoteList()

        list.forEach { element ->
            // If this is first element then copy note text title time and id only one time
            if (list.first() == element) {
                val default = { "" }
                note.id = element.id.ifEmpty(default)
                note.title = element.title.ifEmpty(default)
                note.note = element.note.ifEmpty(default)
                note.time = element.time.ifEmpty(default)
            }
            // This items are copied from each element
            note.viewType = element.viewType
            if (element.listNote.isNotEmpty()) {
                val text = element.listNote[0].note
                val isChecked = element.listNote[0].isChecked
                note.putListNote(text, isChecked)
            }
        }

        return note
    }

    override fun getItemCount() : Int {
        val viewType = getListType(currentDisplayedNotes)
        if (viewType == NoteModel.ONE_NOTE_VIEW_TYPE) {
            // We should always have only 1 item displayed
            return 1
        }
        return currentDisplayedNotes.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : NoteViewHolderBase {
        Log.info(TAG, "onCreateViewHolder() IN")

        val viewHolder = when (viewType) {

            NoteModel.LIST_NOTE_VIEW_TYPE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_edit_list_note, parent, false)
                ListNoteViewHolder(view, this)
            }

            ADD_NEW_NOTE_VIEW_TYPE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_edit_note_add, parent, false)
                AddNewNoteViewHolder(view, this)
            }

            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.editor_note_item, parent, false)
                SimpleNoteViewHolder(view)
            }
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: NoteViewHolderBase, position: Int) {
        Log.info(TAG, "onBindViewHolder() pos = $position")
        holder.bind(getItem(position), position)
    }

    override fun getItemViewType(position: Int): Int {
        if (currentDisplayedNotes.isEmpty()) {
            return -1
        }

        val type = getItem(position).viewType
        Log.detail(TAG, "getItemViewType() view type = $type")
        return type
    }

    private fun getItem(position: Int) = currentDisplayedNotes[position]

    fun prepareEmptyNote() {
        // Display empty note
        val noteList: MutableList<NoteModel> = ArrayList()
        noteList.add(NoteModel.create())
        setDataChanged(noteList)
    }

    fun setNoteData(note: NoteModel) {

        val newList = mutableListOf<NoteModel>()

        val noteList: MutableList<NoteList> = mutableListOf()
        if (note.listNote.isNotEmpty()) {
            val firstElement = note.listNote[0]
            noteList.add(NoteList(firstElement.note, firstElement.isChecked))
        }

        val noteNew = NoteModel.getCopy(note, noteList)
        newList.add(noteNew)

        for ((index, valueNote) in note.listNote.withIndex()) {
            // Only care about second and other elements in note list
            if (index > 0) {
                val noteListCopy: MutableList<NoteList> = mutableListOf()

                val element = note.listNote[index]
                noteListCopy.add(NoteList(element.note, element.isChecked))

                val noteCopy = NoteModel.getCopy(note, noteListCopy)

                newList.add(noteCopy)
            }
        }

        setDataChanged(newList)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setDataChanged(newData: List<NoteModel>) {
        setData(newData.toMutableList())
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setDataChangedForAdd(newData: MutableList<NoteModel>) {
        setData(newData)
        notifyItemChanged(currentDisplayedNotes.size - 1)
    }

    private fun setData(newData: MutableList<NoteModel>) {
        // Add item for 'Add new item' view
        addItemForListViewTypeIfNotFound(newData)
        // Update list
        currentDisplayedNotes = newData
    }

    private fun getListType(list: List<NoteModel>): Int {
        var type = NoteModel.ONE_NOTE_VIEW_TYPE
        if (list.isNotEmpty()) {
            type = list[0].viewType
        }
        return type
    }

    private fun addItemForListViewTypeIfNotFound(list: MutableList<NoteModel>) {
        if (list.isNotEmpty() && getListType(list) == NoteModel.LIST_NOTE_VIEW_TYPE) {
            val last = list.last()
            // Add empty note if it is not found
            if (last.viewType != ADD_NEW_NOTE_VIEW_TYPE) {
                val newNote = NoteModel.EMPTY_NOTE
                newNote.viewType = ADD_NEW_NOTE_VIEW_TYPE
                list.add(newNote)
            }
        }
    }

    fun transformView() {

        val currentList = currentDisplayedNotes

        for (note in currentList) {
            // Change view type
            if (note.viewType == NoteModel.LIST_NOTE_VIEW_TYPE) {
                note.viewType = NoteModel.ONE_NOTE_VIEW_TYPE
            } else if (note.viewType == NoteModel.ONE_NOTE_VIEW_TYPE) {
                note.viewType = NoteModel.LIST_NOTE_VIEW_TYPE
            }
        }

        // Set new data to adapter
        setDataChanged(currentList)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Callbacks from View Holder
    ///////////////////////////////////////////////////////////////////////////

    override fun onDelete(position: Int) {
        currentDisplayedNotes.removeAt(position)
        setDataChanged(currentDisplayedNotes)
    }

    override fun onAdd() {
        val newNote = NoteModel()
        newNote.viewType = NoteModel.LIST_NOTE_VIEW_TYPE
        // Add item before the last item
        currentDisplayedNotes.add(currentDisplayedNotes.lastIndex, newNote)
        // Add a litter delay before updating list
        // for more smooth behavior
        Handler(Looper.getMainLooper()).postDelayed({ setDataChangedForAdd(currentDisplayedNotes) }, 400)
    }
}

///////////////////////////////////////////////////////////////////////////
// View Holder classes
///////////////////////////////////////////////////////////////////////////

// Interface for add/delete action in View
interface UserActionHandler {
    fun onAdd()
    fun onDelete(position: Int)
}

open class NoteViewHolderBase(view: View) : ViewHolder(view) {
    open fun bind(noteModel: NoteModel, position: Int) {}
}

/**
 * View Holder which display one Edit Text and Text Vies
 */
class SimpleNoteViewHolder(view: View) : NoteViewHolderBase(view) {

    private val editView: EditText
    private var noteSaveHelper: NoteSaveHelper = NoteSaveHelper(this)

    init {
        editView = view.findViewById(R.id.note_text)
        editView.addTextChangedListener(TextChangeNotifier(editView.context))
    }

    override fun bind(noteModel: NoteModel, position: Int) {

        // Reset text in case it has spannable strings
        editView.setText(noteModel.note)

        if (noteModel.queryInfo != null) {
            GoodUtils.setTextHighlighting(noteModel.queryInfo!!.rangeForNoteText, editView, noteModel.note)
        }

        // Cache note model associated with this View Holder
        noteSaveHelper.note = noteModel
        editView.removeTextChangedListener(noteSaveHelper)
        editView.addTextChangedListener(noteSaveHelper)
    }
}

/**
 * View Holder which display Edit Text list and Check boxes
 */
class ListNoteViewHolder(view: View, var callback: UserActionHandler) : NoteViewHolderBase(view) {

    private val checkBox: MaterialCheckBox
    private val editView: EditText
    private var noteSaveHelper: NoteSaveHelper = NoteSaveHelper(this)
    private val deleteBtn: ImageButton

    init {
        editView = view.findViewById(R.id.content_edt)
        editView.addTextChangedListener(TextChangeNotifier(editView.context))

        deleteBtn = view.findViewById(R.id.delete_imv)
        deleteBtn.setOnClickListener {
            callback.onDelete(adapterPosition)
        }

        checkBox = view.findViewById(R.id.item_chk)
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            setCrossedOut(isChecked)
            noteSaveHelper.putChecked(isChecked)
        }
    }

    override fun bind(noteModel: NoteModel, position: Int) {
        // Cache note model associated with this View Holder
        noteSaveHelper.note = noteModel
        editView.removeTextChangedListener(noteSaveHelper)
        editView.addTextChangedListener(noteSaveHelper)

        val listNote = noteModel.getNoteList()
        setCrossedOut(listNote.isChecked)
        editView.setText(listNote.note)

        checkBox.isChecked = listNote.isChecked

        if (adapterPosition != 0) {
            deleteBtn.visibility = View.VISIBLE
        } else {
            deleteBtn.visibility = View.INVISIBLE
        }
    }

    private fun setCrossedOut(isChecked: Boolean) {
        if (isChecked) {
            editView.paintFlags = editView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            editView.paintFlags = editView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }
}

/**
 * View holder which displays 'Add list item' view
 */
class AddNewNoteViewHolder(view: View, callback: UserActionHandler) : NoteViewHolderBase(view) {

    private val addBtn: ImageButton

    init {
        addBtn = view.findViewById(R.id.add_imv)
        addBtn.setOnClickListener {
            callback.onAdd()
        }
    }
}

///////////////////////////////////////////////////////////////////////////
// Helper class
///////////////////////////////////////////////////////////////////////////

class NoteSaveHelper(private val viewHolder: NoteViewHolderBase) : TextWatcher {

    var note: NoteModel? = null

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        // no-op
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        // no-op
    }

    override fun afterTextChanged(p0: Editable?) {
        val newText = p0.toString()
        if (viewHolder is SimpleNoteViewHolder) {
            note?.note = newText
        } else {
            note?.let { note: NoteModel ->
                if (note.listNote.isEmpty()) {
                    note.listNote.add(0, NoteList(newText))
                } else {
                    note.listNote[0].note = newText
                }
            }
        }
    }

    fun putChecked(isChecked: Boolean) {
        var note = note?.listNote?.get(0)
        if (note != null) {
            note.isChecked = isChecked
        } else {
            note = NoteList("", isChecked)
        }
        this.note?.listNote?.set(0, note)
    }
}