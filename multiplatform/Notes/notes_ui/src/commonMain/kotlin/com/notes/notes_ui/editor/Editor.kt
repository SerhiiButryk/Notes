package com.notes.notes_ui.editor

interface EditorState {
    fun getHtml(): String
}

interface EditorCommand {
    fun execCommand()
    fun undo()
    fun canUndo(): Boolean = false
    fun isTextInputCommand(): Boolean = false
}