/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.ui.utils

import android.graphics.Paint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.checkbox.MaterialCheckBox
import com.serhii.apps.notes.R
import com.serhii.apps.notes.ui.data_model.NoteModel
import com.serhii.apps.notes.ui.data_model.NoteModel.Companion.getCopy
import com.serhii.core.log.Log

const val TAG = "NoteEditorAdapter"

class NoteEditorAdapter : ListAdapter<NoteModel, NoteViewHolderBase>(DiffListAdapterCallback()) {

    private val ADD_NEW_NOTE_VIEW_TYPE = 101

    private val viewHolders = mutableListOf<NoteViewHolderBase>()

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        viewHolders.clear()
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

        viewHolders.add(viewHolder)

        return viewHolder
    }

    override fun onBindViewHolder(holder: NoteViewHolderBase, position: Int) {
        Log.info(TAG, "onBindViewHolder() pos = $position")
        holder.bind(getItem(position), position)
    }

    override fun submitList(list: MutableList<NoteModel>?) {
        Log.info(TAG, "submitList() sz = ${list?.size}")
        // Add 'add new list item' view item in list
        addEmptyNote(list)
        // Pass new list
        super.submitList(list)
    }

    override fun onCurrentListChanged(previousList: MutableList<NoteModel>, currentList: MutableList<NoteModel>) {
        // Show or hide delete button on item views
        for (holder in viewHolders) {
            holder.update()
        }
    }

    private fun addEmptyNote(list: MutableList<NoteModel>?) {
        list?.let {
            if (list.isNotEmpty() && getListViewType(list) == NoteModel.LIST_NOTE_VIEW_TYPE) {
                val last = list.last()
                if (last.viewType != ADD_NEW_NOTE_VIEW_TYPE) {
                    val newNote = NoteModel.EMPTY_NOTE
                    newNote.viewType = ADD_NEW_NOTE_VIEW_TYPE
                    list.add(newNote)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val type = getItem(position).viewType
        Log.info(TAG, "getItemViewType() view type = $type")
        return type
    }

    override fun getCurrentList(): MutableList<NoteModel> {
        val list = super.getCurrentList()

        if (getListViewType(list) == NoteModel.LIST_NOTE_VIEW_TYPE) {
            val copy = mutableListOf<NoteModel>()
            for (n in list) {
                copy.add(getCopy(n))
            }

            val last: NoteModel = copy.last()
            if (last.viewType == ADD_NEW_NOTE_VIEW_TYPE) {
                copy.remove(last)
            }

            return copy
        }

        return list
    }

    fun getListViewType(list: List<NoteModel>): Int {
        var type = -1
        if (list.isNotEmpty()) {
            type = list[0].viewType
        }
        return type
    }

    fun transformViewType() {
        val currentList: List<NoteModel> = currentList
        val copyList = mutableListOf<NoteModel>()

        for (noteModel in currentList) {
            val copy = getCopy(noteModel)
            // Change view type
            if (copy.viewType == NoteModel.LIST_NOTE_VIEW_TYPE) {
                copy.viewType = NoteModel.ONE_NOTE_VIEW_TYPE
            } else if (copy.viewType == NoteModel.ONE_NOTE_VIEW_TYPE) {
                copy.viewType = NoteModel.LIST_NOTE_VIEW_TYPE
            }
            copyList.add(copy)
        }

        // Set new data to adapter
        submitList(copyList)
    }

    fun onAddNoteClicked() {
        val currentList = currentList
        val newNote = NoteModel()
        newNote.viewType = NoteModel.LIST_NOTE_VIEW_TYPE
        currentList.add(newNote)
        submitList(currentList)
    }

    fun onDeleteClicked(position: Int) {
        Log.info(TAG, "onDeleteClicked() position = $position")
    }
}

open class NoteViewHolderBase(view: View) : ViewHolder(view) {
    open fun bind(noteModel: NoteModel, position: Int) {}
    open fun update() {}
}

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

class ListNoteViewHolder(view: View, adapter: NoteEditorAdapter) : NoteViewHolderBase(view) {

    private val checkBox: MaterialCheckBox
    private val editView: EditText
    private var noteSaveHelper: NoteSaveHelper = NoteSaveHelper(this)
    private val deleteBtn: ImageButton

    init {
        editView = view.findViewById(R.id.content_edt)
        deleteBtn = view.findViewById(R.id.delete_imv)
        deleteBtn.setOnClickListener {
            adapter.onDeleteClicked(adapterPosition)
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
    }

    override fun update() {
        if (adapterPosition != 0)
            deleteBtn.visibility = View.VISIBLE

        editView.requestFocus()
    }

    private fun setCrossedOut(isChecked: Boolean) {
        if (isChecked) {
            editView.paintFlags = editView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            editView.paintFlags = editView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }
}

class AddNewNoteViewHolder(view: View, adapter: NoteEditorAdapter) : NoteViewHolderBase(view) {

    val addBtn: ImageButton

    init {
        addBtn = view.findViewById(R.id.add_imv)
        addBtn.setOnClickListener {
            adapter.onAddNoteClicked()
        }
    }

    override fun bind(noteModel: NoteModel, position: Int) {

    }
}

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
            note?.putListNote(newText)
        }
    }

    fun putChecked(isChecked: Boolean) {
        note?.putListNoteChecked(isChecked)
    }
}

class DiffListAdapterCallback : DiffUtil.ItemCallback<NoteModel>() {

    override fun areItemsTheSame(oldItem: NoteModel, newItem: NoteModel): Boolean {
        return oldItem.id == newItem.id && oldItem.viewType == newItem.viewType
    }

    override fun areContentsTheSame(oldItem: NoteModel, newItem: NoteModel): Boolean {
        return oldItem.note == newItem.note && oldItem.title == newItem.title
    }
}