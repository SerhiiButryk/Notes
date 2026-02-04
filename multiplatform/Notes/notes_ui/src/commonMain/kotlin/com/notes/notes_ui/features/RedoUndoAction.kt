package com.notes.notes_ui.features

import com.notes.notes_ui.editor.EditorCommand

/**
 * Class which implements basic redo/undo functionality in text editor
 */
class RedoUndoAction {
    private val commands: MutableList<EditorCommand> = mutableListOf()

    // Holds a command that we can undo
    private var undoCommand: EditorCommand? = null

    // Holds a command that we can reapply
    private var lastReappliedCommand: EditorCommand? = null
    private var applyFirst = false
    private var pickLast = true
    private var ignoreNextCommand = false

    fun onEditAction(command: EditorCommand) {

        // Do not track the first editor command
        // this is the actual note we are opening in the editor
        if (!command.canUndo()) return

        // This is not user action and we ignore this
        if (ignoreNextCommand) { ignoreNextCommand = false; return }

        pickLast = true
        lastReappliedCommand = null
        commands.add(command)
    }

    fun clear() {
        commands.clear()
        undoCommand = null
        lastReappliedCommand = null
        applyFirst = false
        pickLast = true
    }

    fun undoAction(): Boolean {

        val command: EditorCommand? = if (pickLast) {
            pickLast = false
            commands.lastOrNull()
        } else {
            val currIndex = commands.indexOf(undoCommand)
            commands.getOrNull(currIndex - 1)
        }

        if (command == null) return false

        undoCommand = command
        command.undo()
        lastReappliedCommand = undoCommand
        applyFirst = true
        ignoreNextCommand = true

        return true
    }

    fun reapplyAction(): Boolean {

        if (lastReappliedCommand == null) {
            // Can't reapply
            return false
        }

        val command: EditorCommand? = if (applyFirst) {
            applyFirst = false
            lastReappliedCommand
        } else {
            val currIndex = commands.indexOf(lastReappliedCommand)
            lastReappliedCommand = commands.getOrNull(currIndex + 1)
            lastReappliedCommand
        }

        if (command == null) return false

        lastReappliedCommand!!.execCommand()
        ignoreNextCommand = true

        return true
    }
}
