package com.notes.notes_ui.data

import com.notes.notes_ui.NotesViewModel.Notes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun getNoteList(): Flow<List<Notes>> {
    return flow {
        emit(getFakeList())
    }
}

private fun getFakeList(): List<Notes> {
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
        ),
        Notes("Note 5 \n Some note 1"),
        Notes("Note 6 \n Some note 2"),
        Notes(
            "Note 7 \n Some note 4 Some note 4  Some note 4 \n" +
                    "Some note 4 Some note 4 \n"
        ),
        Notes(
            "Note 8 \n Some note 4 Some note 4  Some note 4 \nSome note 4 Some note 4 \nSome note 4 Some note 4  \n" +
                    " Some note 4 Some note 4  Some note 4 \n" +
                    "Some note 4 Some note 4 \n" +
                    "Some note 4 Some note 4 "
        ),
        Notes("Note 9 \n Some note 1"),
        Notes("Note 10 \n Some note 2"),
        Notes(
            "Note 11 \n Some note 4 Some note 4  Some note 4 \n" +
                    "Some note 4 Some note 4 \n"
        ),
        Notes(
            "Note 12 \n Some note 4 Some note 4  Some note 4 \nSome note 4 Some note 4 \nSome note 4 Some note 4  \n" +
                    " Some note 4 Some note 4  Some note 4 \n" +
                    "Some note 4 Some note 4 \n" +
                    "Some note 4 Some note 4 "
        ),
        Notes("Note 13 \n Some note 1"),
        Notes("Note 14 \n Some note 2"),
        Notes(
            "Note 15 \n Some note 4 Some note 4  Some note 4 \n" +
                    "Some note 4 Some note 4 \n"
        ),
        Notes(
            "Note 16 \n Some note 4 Some note 4  Some note 4 \nSome note 4 Some note 4 \nSome note 4 Some note 4  \n" +
                    " Some note 4 Some note 4  Some note 4 \n" +
                    "Some note 4 Some note 4 \n" +
                    "Some note 4 Some note 4 "
        ),
        Notes("Note 17 \n Some note 1"),
        Notes("Note 18 \n Some note 2"),
        Notes(
            "Note 19 \n Some note 4 Some note 4  Some note 4 \n" +
                    "Some note 4 Some note 4 \n"
        ),
        Notes(
            "Note 20 \n Some note 4 Some note 4  Some note 4 \nSome note 4 Some note 4 \nSome note 4 Some note 4  \n" +
                    " Some note 4 Some note 4  Some note 4 \n" +
                    "Some note 4 Some note 4 \n" +
                    "Some note 4 Some note 4 "
        )
    )
    return fakeList
}