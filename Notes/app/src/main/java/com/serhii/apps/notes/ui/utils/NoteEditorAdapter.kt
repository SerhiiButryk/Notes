/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.ui.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.serhii.apps.notes.R
import com.serhii.apps.notes.ui.data_model.NoteModel
import com.serhii.core.log.Log

const val TAG = "NoteEditorAdapter"

open class NoteViewHolderBase(view: View) : RecyclerView.ViewHolder(view) {
    open fun bind(noteModel: NoteModel) {}
}

class SimpleNoteViewHolder(view: View) : NoteViewHolderBase(view) {

    private val editView: EditText
    private var noteModelCached: NoteModel? = null

    init {
        editView = view.findViewById(R.id.note_text)
        editView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // no-op
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // no-op
            }

            override fun afterTextChanged(p0: Editable?) {
                val newText = p0.toString()
                noteModelCached?.note = newText
            }
        })
    }

    override fun bind(noteModel: NoteModel) {
        editView.setText(noteModel.note)
        // Cache note model associated with this View Holder
        noteModelCached = noteModel
    }

}

class NoteEditorAdapter : ListAdapter<NoteModel, NoteViewHolderBase>(DiffListAdapterCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : NoteViewHolderBase {
        Log.info(TAG, "onCreateViewHolder()")
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.editor_note_item, parent, false)
        return SimpleNoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolderBase, position: Int) {
        Log.info(TAG, "onBindViewHolder() pos = $position")
        holder.bind(getItem(position))
    }

    override fun submitList(list: MutableList<NoteModel>?) {
        Log.info(TAG, "submitList() sz = ${list?.size}")
        super.submitList(list)
    }

    override fun submitList(list: MutableList<NoteModel>?, commitCallback: Runnable?) {
        Log.info(TAG, "submitList() sz = ${list?.size}")
        super.submitList(list, commitCallback)
    }

}