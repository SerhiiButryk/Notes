package com.app.server.main_app_service.notes.data

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface NotesRepository : MongoRepository<Note, ObjectId> {
    fun findByOwnerId(ownerId: ObjectId): List<Note>
}