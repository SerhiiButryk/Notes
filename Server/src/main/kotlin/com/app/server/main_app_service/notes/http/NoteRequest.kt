package com.app.server.main_app_service.notes.http

data class NoteRequest(
    val id: String?,
    val title: String,
    val content: String
)