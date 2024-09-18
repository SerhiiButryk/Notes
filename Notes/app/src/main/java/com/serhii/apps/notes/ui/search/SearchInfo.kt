/*
 * Copyright 2023. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.ui.search

import androidx.lifecycle.MutableLiveData
import com.serhii.apps.notes.ui.data_model.NoteModel
import com.serhii.core.log.Log
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

data class SearchInfo(val rangeForNoteTitle: List<IntRange>,
                      val rangeForNoteText: List<IntRange>)

suspend fun search(query: String, notesForSearch: List<NoteModel>, results: MutableLiveData<List<NoteModel>>) = coroutineScope {
    Log.info(message = "search()")

    // Remove extra spaces
    val searchedText = query.trim()

    // Iterate over the list and search notes with passed query
    val foundNotes = notesForSearch.filter { element ->
        Log.info(message = "performSearch() note = '${element.id}'")

        val noteText = element.plainText
        val noteTitle = element.title

        val rangeItemTitleResult: Deferred<List<IntRange>> = async(CoroutineName("itemTitle")) {
            searchAllOccurrences(noteTitle, searchedText)
        }

        val rangeItemNoteTextResult: Deferred<List<IntRange>> = async(CoroutineName("itemNote")) {
            searchAllOccurrences(noteText, searchedText)
        }

        // Wait for results
        val rangeItemTitle = rangeItemTitleResult.await()
        val rangeItemNoteText = rangeItemNoteTextResult.await()

        element.queryInfo = SearchInfo(rangeItemTitle, rangeItemNoteText)

        // Add to the list if any range is not empty
        rangeItemTitle.isNotEmpty() || rangeItemNoteText.isNotEmpty()
    }

    results.postValue(foundNotes)

    Log.info(message = "performSearch() done")
}

private fun searchAllOccurrences(text: String, searchQuery: String): List<IntRange> {

    val ignoreCase = true
    var start = 0
    var index = 0

    val foundRanges = mutableListOf<IntRange>()

    while (true) {
        index = text.indexOf(searchQuery, start, ignoreCase)

        if (index == -1)
            break

        val range = index..(searchQuery.length + index);
        foundRanges.add(range)

        if ((index + searchQuery.length - 1) >= text.length) {
            break
        }

        start = index + searchQuery.length
    }

    return foundRanges
}