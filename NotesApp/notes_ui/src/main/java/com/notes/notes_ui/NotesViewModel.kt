package com.notes.notes_ui

import android.os.Parcelable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.parcelize.Parcelize
import kotlin.random.Random

class NotesViewModel : ViewModel() {

    // This annotation could be redundant as
    // the class is already stable, because all properties are stable.
    // However, keep it for clarity.
    @Stable
    @Parcelize
    data class Notes(val content: String = "", val id: String = Random.nextLong(1000).toString()) :
        Parcelable {

        companion object {
            fun EmptyNote() = Notes("", "")
        }
    }

    private val _notesState = MutableStateFlow(listOf<Notes>())
    val notesState = _notesState.asStateFlow()

    init {

        val fakeList = listOf(
            Notes("Note 1 \n Some note 1"),
            Notes("Note 2 \n Some note 2"),
            Notes(
                "Note 3 \n Some note 4 Some note 4  Some note 4 \n" +
                        "Some note 4 Some note 4 \n"
            ),
            Notes(
                "Note 4 \n Some note 4 Some note 4  Some note 4 \nSome note 4 Some note 4 \nSome note 4 Some note 4  \n" +
                        " Some note 4 Some note 4  Some note 4 \n" +
                        "Some note 4 Some note 4 \n" +
                        "Some note 4 Some note 4 "
            )
        )

        _notesState.value = fakeList
    }

}