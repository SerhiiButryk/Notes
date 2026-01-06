package com.notes.notes_ui.feature

import com.notes.notes_ui.EditorCommand

/**
 * Class which implements redo/undo functionality in text editor
 */
class RedoUndoAction {
    private val commands: MutableList<EditorCommand> = mutableListOf()

    // Holds a command that we can undo
    private var undoCommand: EditorCommand? = null

    // Holds a command that we can reapply
    private var reappliedCommand: EditorCommand? = null
    private var applyFirst = false
    private var revertedFirst = false

    fun hasUndoAction(): Boolean = undoCommand != null

    fun hasRedoAction(): Boolean = reappliedCommand != null

    fun onEditAction(command: EditorCommand) {
        revertedFirst = false
        reappliedCommand = null
        commands.add(command)
    }

    fun clearStates() {
        commands.clear()
        undoCommand = null
        reappliedCommand = null
        applyFirst = false
        revertedFirst = false
    }

    fun undoAction(): Boolean {
        // Undo last
        if (!revertedFirst) {
            val command = commands.lastOrNull() ?: return false
            undoCommand = command
            command.undo()
            reappliedCommand = undoCommand
            applyFirst = true
            revertedFirst = true
            return true
        }
        // Get previous
        val currIndex = commands.indexOf(undoCommand)
        val previous = commands.getOrNull(currIndex - 1)
        if (previous != null) {
            undoCommand = previous
            previous.undo()
            reappliedCommand = undoCommand
            applyFirst = true
            return true
        } else {
            // Went to the start, nothing to undo
            return false
        }
    }

    fun reapplyAction(): Boolean {
        if (reappliedCommand == null) {
            // Can't reapply
            return false
        }

        if (applyFirst) {
            reappliedCommand!!.execCommand()
            applyFirst = false
            return true
        }

        val currIndex = commands.indexOf(reappliedCommand)
        val next = commands.getOrNull(currIndex + 1)
        if (next != null) {
            reappliedCommand = next
            reappliedCommand!!.execCommand()
            return true
        } else {
            // Went to the end, nothing to redo
            return false
        }
    }
}
