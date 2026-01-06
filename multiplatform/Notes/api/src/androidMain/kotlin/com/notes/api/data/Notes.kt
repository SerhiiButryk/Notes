package com.notes.api.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Notes(
    val content: String = "",
    val id: Long = 0,
    val userId: String = "",
    val time: String = "",
    val pendingUpdate: Boolean = true,
) : Parcelable {
    companion object {
        fun NewNote() = Notes(id = -1)

        fun AbsentNote() = Notes(id = -2)

        fun DeletedNote() = Notes(id = -3)
    }
}
