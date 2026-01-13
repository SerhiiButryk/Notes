package com.notes.services.storage

import api.PlatformAPIs.logger
import api.data.Document
import api.provideAuthService
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Service which implements database storage with Google Firestore server.
 *
 * Basic concepts of firestore:
 *
 * Google defines 'collection' and 'document'. 'Collection' is like a folder,
 * and 'document' is like a database file which actually stores data.
 * We can access a document using a path like 'users/{userId}/user_notes/{noteId}'.
 *
 * Permissions can be controlled by special rules on firebase console.
 */

class FirebaseFirestore {
    private val tag = "FirebaseFirestore"

    private val database = Firebase.firestore

    suspend fun store(
        name: String,
        value: String,
    ): Boolean = storeImpl(name, value)

    suspend fun load(name: String): String? = loadImpl(name)

    suspend fun fetchAll(): List<Document> {
        logger.logi("$tag::fetchAll()")

        if (!isAuthenticated()) return emptyList()

        val authService = provideAuthService()
        val uid = authService.getUserId()

        return suspendCancellableCoroutine { continuation ->
            database
                .collection("users/$uid/user_notes")
                .get()
                .addOnSuccessListener { result ->
                    logger.logi("$tag::fetchAll() got size = ${result.size()}")
                    val list = mutableListOf<Document>()
                    for (document in result) {
                        logger.logi("$tag::fetchAll() <= ${document.id}")
                        val content = document.data.getValue("content") as String
                        val doc = Document(data = content, name = document.id)
                        list.add(doc)
                    }
                    continuation.resume(list)
                }.addOnFailureListener { e ->
                    logger.loge("$tag::fetchAll() error: $e")
                    continuation.resume(emptyList())
                }
        }
    }

    suspend fun delete(name: String): Boolean {
        logger.logi("$tag::delete()")

        if (!isAuthenticated()) return false

        if (name.isEmpty()) {
            logger.loge("$tag::delete() stop cos empty name")
            return false
        }

        try {
            val noteIdCheck = name.toInt()
            if (noteIdCheck < 0) {
                logger.loge("$tag::delete() stop cos not valid note id")
                return false
            }
        } catch (e: NumberFormatException) {
            logger.loge("$tag::delete() stop cos not a number")
            return false
        }

        val authService = provideAuthService()
        val uid = authService.getUserId()

        return suspendCancellableCoroutine { continuation ->
            database
                .document("users/$uid/user_notes/$name")
                .delete()
                .addOnSuccessListener {
                    logger.logi("$tag::delete() success, deleted note = $name")
                    continuation.resume(true)
                }.addOnFailureListener { e ->
                    logger.loge("$tag::delete() failed to delete note = $name")
                    continuation.resume(false)
                }
        }
    }

    private suspend fun storeImpl(
        name: String,
        data: String,
    ): Boolean {
        val authService = provideAuthService()
        val uid = authService.getUserId()

        logger.logi("$tag::storeImpl() noteId = $name")

        if (!isAuthenticated()) return false

        if (name.isEmpty() || data.isEmpty()) {
            logger.loge("$tag::storeImpl() stop cos empty data")
            return false
        }

        try {
            val noteIdCheck = name.toInt()
            if (noteIdCheck < 0) {
                logger.loge("$tag::storeImpl() stop cos not valid note id")
                return false
            }
        } catch (e: NumberFormatException) {
            logger.loge("$tag::storeImpl() stop cos not a number")
            return false
        }

        return suspendCancellableCoroutine { continuation ->

            val noteId = name

            val newData =
                hashMapOf(
                    "content" to data,
                )

            // Matches the location:
            // users/{userId}/user_notes/{noteId}

            val rootUsersFolder = database.collection("users")
            val userDocument = rootUsersFolder.document(uid)

            val userNotesFolder = userDocument.collection("user_notes")
            val userNoteDocument = userNotesFolder.document(noteId)

            userNoteDocument
                .set(newData)
                .addOnSuccessListener {
                    logger.logi("$tag::storeImpl() note '$noteId' added")
                    continuation.resume(true)
                }.addOnFailureListener { e ->
                    logger.loge("$tag::storeImpl() failed, error: $e")
                    continuation.resume(false)
                }
        }
    }

    private suspend fun loadImpl(name: String): String? {
        val authService = provideAuthService()
        val uid = authService.getUserId()

        if (!isAuthenticated()) return null

        if (name.isEmpty()) {
            logger.loge("$tag::loadImpl() stop cos empty data")
            return null
        }

        try {
            val noteIdCheck = name.toInt()
            if (noteIdCheck < 0) {
                logger.loge("$tag::loadImpl() stop cos not valid note id")
                return null
            }
        } catch (e: NumberFormatException) {
            logger.loge("$tag::loadImpl() stop cos not a number")
            return null
        }

        return suspendCancellableCoroutine { continuation ->

            val noteId = name

            // Get the location:
            // users/{userId}/user_notes/{noteId}

            database
                .document("users/$uid/user_notes/$noteId")
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val content = document.getString("content")
                        if (content == null) {
                            logger.loge("$tag::loadImpl() no content")
                            continuation.resume(null)
                        } else {
                            logger.logi("$tag::loadImpl() got content")
                            continuation.resume(content)
                        }
                    }
                }.addOnFailureListener { e ->
                    logger.loge("$tag::loadImpl() failed, error: $e")
                    continuation.resume(null)
                }
        }
    }

    private fun isAuthenticated(): Boolean {
        val authService = provideAuthService()
        val uid = authService.getUserId()

        if (!authService.isAuthenticated()) {
            logger.loge("$tag::isAuthenticated() not authenticated")
            return false
        }

        if (uid.isEmpty()) {
            logger.loge("$tag::isAuthenticated() uid is invalid")
            return false
        }

        return true
    }
}
