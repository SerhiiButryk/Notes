package com.notes.notes_ui.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextRange
import api.Platform
import api.data.Notes
import dev.mkeeda.arranger.richtext.RichString
import dev.mkeeda.arranger.richtext.editor.RichTextState
import dev.mkeeda.arranger.richtext.html.fromHtml
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.CoroutineContext

fun EditorState(richString: Any?): RichTextState {
    val state = RichTextState(
        initialText = richString as? RichString ?: RichString(""),
        initialSelection = TextRange.Zero,
    )
    return state
}

@Composable
fun rememberRichEditorState(notes: Notes) =
    remember(notes.content, notes.richState) {
        if (notes.richState is RichTextState) {
            mutableStateOf(notes.richState as RichTextState)
        } else {
            mutableStateOf(EditorState(notes.richState))
        }
    }

fun Flow<List<Notes>>.mapToHtml(context: CoroutineContext): Flow<List<Notes>> {
    return flow {
        collect { data ->
            val list = mutableListOf<Notes>()
            data.forEach { note ->
                Platform().logger.logi("mapToHtml(): Parsing note ('${note.id}')...")
                note.richState = RichString.fromHtml(note.content)
                list.add(note)
            }
            emit(list)
        }
    }.flowOn(context)
}
