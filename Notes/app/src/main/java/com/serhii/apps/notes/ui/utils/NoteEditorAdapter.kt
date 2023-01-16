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

const val TAG = "NoteEditorAdapter"

/**
 * Adapter for managing views and note data in Editor Note Fragment
 */
class NoteEditorAdapter : RecyclerView.Adapter<NoteViewHolderBase>(), UserActionHandler {

    /**
     * This is a type of user list note
     */
    private val ADD_NEW_NOTE_VIEW_TYPE = 101

    private var notesData = mutableListOf<NoteModel>()
    private var userNotesList = mutableListOf<NoteModel>()

    fun getCurrentList(): MutableList<NoteModel> {
        return notesData
    }

    /**
     * Return note data which Recycle View displays
     */
    fun getNoteList() : MutableList<NoteModel> {
        // We need to remove empty note which we creates for displaying 'Add item list' option
        if (notesData.isNotEmpty()) {
            // Clear as we are going to add new data
            userNotesList.clear()
            // Iterate through data list and gets note
            for (note in notesData) {
                if (note.viewType != ADD_NEW_NOTE_VIEW_TYPE)
                    userNotesList.add(NoteModel.getCopy(note))
            }
            return userNotesList
        }
        // Else return as it is
        return notesData
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

        for (n in list) {
            if (n.id.isNotEmpty()) {
                note.id = n.id
            }
            if (n.title.isNotEmpty()) {
                note.title = n.title
            }
            if (n.note.isNotEmpty()) {
                note.note = n.note
            }
            if (n.time.isNotEmpty()) {
                note.time = n.time
            }
            note.viewType = n.viewType
            if (n.listNote.isNotEmpty()) {
                val text = n.listNote[0].note
                val isChecked = n.listNote[0].isChecked
                note.putListNote(text, isChecked)
            }
        }

        return note
    }

    override fun getItemCount() : Int {
        val viewType = getListType(notesData)
        if (viewType == NoteModel.ONE_NOTE_VIEW_TYPE) {
            // We should always have only 1 item displayed
            return 1
        }
        return notesData.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : NoteViewHolderBase {
        Log.info(TAG, "onCreateViewHolder()")

        val viewHolder: NoteViewHolderBase

        if (viewType == NoteModel.LIST_NOTE_VIEW_TYPE) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_edit_list_note, parent, false)
            viewHolder = ListNoteViewHolder(view, this)
        } else if (viewType == ADD_NEW_NOTE_VIEW_TYPE) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_edit_note_add, parent, false)
            viewHolder = AddNewNoteViewHolder(view, this)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.editor_note_item, parent, false)
            viewHolder = SimpleNoteViewHolder(view)
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: NoteViewHolderBase, position: Int) {
        Log.info(TAG, "onBindViewHolder() pos = $position")
        holder.bind(getItem(position), position)
    }

    override fun getItemViewType(position: Int): Int {
        if (notesData.isEmpty())
            return -1
        val type = getItem(position).viewType
        Log.info(TAG, "getItemViewType() view type = $type")
        return type
    }

    private fun getItem(position: Int) = notesData[position]

    fun prepareEmptyNote() {
        // Display empty note
        val noteList: MutableList<NoteModel> = ArrayList()
        noteList.add(NoteModel.create())
        setDataChanged(noteList)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setDataChanged(newData: MutableList<NoteModel>) {
        setData(newData)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setDataChangedForAdd(newData: MutableList<NoteModel>) {
        setData(newData)
        notifyItemChanged(notesData.size - 1)
    }

    private fun setData(newData: MutableList<NoteModel>) {
        // Add item for 'Add new item' view
        addItemForListViewTypeIfNotFound(newData)
        // Update list
        notesData = newData
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

        val currentList = notesData

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
        notesData.removeAt(position)
        setDataChanged(notesData)
    }

    override fun onAdd() {
        val newNote = NoteModel()
        newNote.viewType = NoteModel.LIST_NOTE_VIEW_TYPE
        // Add item before the last item
        notesData.add(notesData.lastIndex, newNote)
        // Add a litter delay before updating list
        // for more smooth behavior
        Handler(Looper.getMainLooper()).postDelayed({ setDataChangedForAdd(notesData) }, 400)
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
    }

    override fun bind(noteModel: NoteModel, position: Int) {
        editView.setText(noteModel.note)
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