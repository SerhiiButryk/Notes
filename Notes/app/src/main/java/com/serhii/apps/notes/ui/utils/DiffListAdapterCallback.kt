/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.ui.utils

import androidx.recyclerview.widget.DiffUtil
import com.serhii.apps.notes.ui.data_model.NoteModel

class DiffListAdapterCallback : DiffUtil.ItemCallback<NoteModel>() {

    override fun areItemsTheSame(oldItem: NoteModel, newItem: NoteModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: NoteModel, newItem: NoteModel): Boolean {
        return oldItem.note == newItem.note && oldItem.title == newItem.title
    }
}