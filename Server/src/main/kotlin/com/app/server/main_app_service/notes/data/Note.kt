package com.app.server.main_app_service.notes.data

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("notes")
data class Note(
    val title: String,
    val content: String,
    val createdAt: Instant,
    @Id val id: ObjectId = ObjectId(),
    val ownerId: ObjectId
)