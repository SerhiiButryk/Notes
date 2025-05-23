package com.app.server.main_app_service.notes.http

import java.time.Instant

data class NoteResponse(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: Instant
)