/*
 * Copyright 2023. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.control.backup.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true) // Need to do proper serialization/deserialization
class BackupAdapter(val notes: List<NoteAdapter>)

@JsonClass(generateAdapter = true) // Need to do proper serialization/deserialization
class NoteAdapter(val id: String, val note: String)