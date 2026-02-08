package com.notes.data

import android.os.Build
import api.data.Notes
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

fun Notes.toEntity(setId: Boolean = true): NoteEntity {
    val time: String =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            LocalDateTime.now().format(formatter)
        } else {
            Calendar.getInstance().getTime().toString()
        }
    return if (setId) {
        NoteEntity(
            uid = id,
            userId = userId,
            content = content,
            time = time
        )
    } else {
        NoteEntity(userId = userId, content = content, time = time)
    }
}

fun NoteEntity.toNote(): Notes = Notes(content = content, id = uid, userId = userId, time = time)