package com.notes.notes_ui.editor

import api.Platform
import api.data.Notes
import dev.mkeeda.arranger.richtext.RichString
import dev.mkeeda.arranger.richtext.editor.RichTextState
import dev.mkeeda.arranger.richtext.html.fromHtml
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.CoroutineContext

fun createEditorState(richString: Any?): RichTextState =
    RichTextState(
        initialText = richString as? RichString ?: RichString(""),
    )

fun Flow<List<Notes>>.mapToHtml(context: CoroutineContext): Flow<List<Notes>> {
    return flow {
        collect { data ->
            val list = mutableListOf<Notes>()
            data.forEach { note ->
                Platform().logger.logi("mapToHtml(): Parsing note ('${note.id}')...")
                note.richString = RichString.fromHtml(note.content)
                list.add(note)
            }
            emit(list)
        }
    }.flowOn(context)
}
