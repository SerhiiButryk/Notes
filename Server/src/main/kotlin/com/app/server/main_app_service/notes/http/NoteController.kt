package com.app.server.main_app_service.notes.http

import com.app.server.main_app_service.notes.data.Note
import com.app.server.main_app_service.notes.data.NotesRepository
import com.app.server.main_app_service.security.Credentials
import org.bson.types.ObjectId
import org.springframework.web.bind.annotation.*
import java.time.Instant

// Handles the next requests:
// POST http://localhost:port/notes
// GET http://localhost:port/notes
// DELETE http://localhost:port/notes/6821e3f6f54e0b442419e966

@RestController
@RequestMapping("/notes")
class NoteController(
    private val repository: NotesRepository
) {

    // POST http://localhost:port/notes

    @PostMapping
    fun save(
        @RequestBody request: NoteRequest
    ): NoteResponse {

        val ownerId = Credentials.getAuthenticatedUserId()

        val note = repository.save(
            Note(
                request.title,
                request.content,
                Instant.now(),
                request.id?.let { ObjectId(it) } ?: ObjectId(),
                ObjectId(ownerId)
            )
        )

        return NoteResponse(note.id.toHexString(), note.title, note.content, note.createdAt)
    }

    // GET http://localhost:port/notes

    @GetMapping
    fun findByOwnerId(): List<NoteResponse> {

        val ownerId = Credentials.getAuthenticatedUserId()

        return repository.findByOwnerId(ObjectId(ownerId)).map { note ->
            NoteResponse(note.id.toHexString(), note.title, note.content, note.createdAt)
        }
    }

    // DELETE http://localhost:port/notes/6821e3f6f54e0b442419e966

    @DeleteMapping(path = ["/{id}"])
    fun deleteById(@PathVariable id: String) {

        val ownerId = Credentials.getAuthenticatedUserId()

        val note = repository.findById(ObjectId(id)).orElseThrow {
            throw IllegalArgumentException("Not found")
        }

        if (note.id.toHexString() != ownerId) {
            throw IllegalArgumentException("User is not the owner. Can't delete this note.")
        }

        repository.deleteById(ObjectId(id))
    }

}