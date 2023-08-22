/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.utils

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.serhii.apps.notes.R
import com.serhii.apps.notes.ui.data_model.NoteModel
import com.serhii.apps.notes.ui.search.SearchableInfo
import com.serhii.apps.notes.ui.utils.NoteListAdapter.NoteViewHolder
import com.serhii.core.utils.GoodUtils

/**
 * Adapter for displaying notes on preview screen
 */
class NoteListAdapter(private val clickListener: NoteViewHolder.ClickListener) : RecyclerView.Adapter<NoteViewHolder>() {

    private var notes: List<NoteModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item_view, parent, false)
        return NoteViewHolder(view, object : ClickListener {
            override fun onClick(position: Int) {
                clickListener.onClick(notes[position])
            }
        })
    }

    override fun onBindViewHolder(noteViewHolder: NoteViewHolder, i: Int) {
        noteViewHolder.setTitle(notes[i].title, notes[i].queryInfo)
        noteViewHolder.setDescription(notes[i].note, notes[i].queryInfo)
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setDataChanged(noteModels: List<NoteModel>) {
        notes = noteModels
        notifyDataSetChanged()
    }

    class NoteViewHolder(itemView: View, clickListener: NoteListAdapter.ClickListener?) :
        RecyclerView.ViewHolder(itemView) {

        private val title: TextView
        private val description: TextView

        init {
            title = itemView.findViewById(R.id.tv_note_title)
            description = itemView.findViewById(R.id.tv_note_description)
            if (clickListener != null) {
                itemView.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        clickListener.onClick(position)
                    }
                }
            }
        }

        fun setTitle(title: String, queryInfo: SearchableInfo?) {
            if (queryInfo != null) {
                GoodUtils.setTextHighlighting(queryInfo.rangeItemTitle, this.title, title)
            } else {
                this.title.text = title
            }
        }

        fun setDescription(description: String, queryInfo: SearchableInfo?) {
             if (queryInfo != null) {
                GoodUtils.setTextHighlighting(queryInfo.rangeItemNoteText, this.description, description)
            } else {
                 this.description.text = description
            }
        }

        interface ClickListener {
            fun onClick(noteModel: NoteModel?)
        }
    }

    interface ClickListener {
        fun onClick(position: Int)
    }
}