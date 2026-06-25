package com.notes.services.storage

import api.AppService.Companion.FIREBASE_STORAGE
import api.AppServices
import api.Platform
import api.data.AbstractStorageService
import api.data.Document
import api.data.toDocument
import api.data.toJson
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Service which implements database storage with Google Firestore server.
 *
 * Basic concepts of firestore:
 *
 * Google defines 'collection' and 'document'. The 'Collection' is like a folder,
 * and the 'document' is like a database file which actually stores data.
 * We can access a document using a path like 'users/{userId}/user_notes/{noteId}'.
 *
 * Access permissions of 'collection' and 'document' are be managed
 * by special rules on firebase console.
 *
 * Limitations:
 * We can store anly data like a Map or a POJO java classes.
 */

class FirebaseFirestore : AbstractStorageService() {

    private val tag = "FirebaseFirestore"
    private val database = Firebase.firestore

    override val key = FIREBASE_STORAGE

    override var canUse: Boolean = false
        get() = isAuthenticated()

    override suspend fun store(document: Document): Boolean = storeImpl(document)

    override suspend fun load(document: Document): Document? = loadImpl(document)

    override suspend fun fetchAll(): List<Document> {
        Platform().logger.logi("$tag::fetchAll()")

        if (!isAuthenticated()) return emptyList()

        val authService = AppServices.getDefaultAuthService()
        val uid = authService.getUserId()

        return suspendCancellableCoroutine { continuation ->
            database
                .collection("users/$uid/user_notes")
                .get()
                .addOnSuccessListener { snapshots ->
                    Platform().logger.logi("$tag::fetchAll() size = ${snapshots.size()}")
                    val list = mutableListOf<Document>()
                    for (snapshot in snapshots) {
                        Platform().logger.logi("$tag::fetchAll() done for ${snapshot.id}")
                        val json = snapshot.data["content"] as? String ?: ""
                        val doc = json.toDocument()
                        if (!doc.isEmpty()) {
                            list.add(doc)
                        }
                    }
                    continuation.resume(list)
                }.addOnFailureListener { e ->
                    Platform().logger.loge("$tag::fetchAll() error: $e")
                    continuation.resume(emptyList())
                }
        }
    }

    override suspend fun delete(document: Document): Boolean {

        if (!paramsCheck(document.name)) {
            return false
        }

        val authService = AppServices.getDefaultAuthService()
        val uid = authService.getUserId()

        return suspendCancellableCoroutine { continuation ->
            database
                .document("users/$uid/user_notes/${document.name}")
                .delete()
                .addOnSuccessListener {
                    Platform().logger.logi("$tag::delete($${document.name}) done")
                    continuation.resume(true)
                }.addOnFailureListener { e ->
                    Platform().logger.loge("$tag::delete($${document.name}) failed, error: $e")
                    e.printStackTrace()
                    continuation.resume(false)
                }
        }
    }

    private suspend fun storeImpl(
        document: Document
    ): Boolean {
        val authService = AppServices.getDefaultAuthService()
        val uid = authService.getUserId()

        if (!paramsCheck(document.name)) {
            return false
        }

        return suspendCancellableCoroutine { continuation ->

            val payload = hashMapOf("content" to document.toJson())

            // Matches the location:
            // users/{userId}/user_notes/{noteId}

            val rootUsersFolder = database.collection("users")
            val userDocument = rootUsersFolder.document(uid)

            val userNotesFolder = userDocument.collection("user_notes")
            val userNoteDocument = userNotesFolder.document(document.name)

            userNoteDocument
                .set(payload)
                .addOnSuccessListener {
                    Platform().logger.logi("$tag::storeImpl(${document.name}) done")
                    continuation.resume(true)
                }.addOnFailureListener { e ->
                    Platform().logger.loge("$tag::storeImpl(${document.name}) failed, error: $e")
                    continuation.resume(false)
                }
        }
    }

    private suspend fun loadImpl(document: Document): Document? {
        val authService = AppServices.getDefaultAuthService()
        val uid = authService.getUserId()

        if (!paramsCheck(document.name)) {
            return null
        }

        return suspendCancellableCoroutine { continuation ->

            val noteId = document.name

            // Get the location:
            // users/{userId}/user_notes/{noteId}

            database
                .document("users/$uid/user_notes/$noteId")
                .get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot != null && snapshot.exists()) {
                        val document = snapshot.toString().toDocument()
                        Platform().logger.loge("$tag::loadImpl($noteId) done")
                        continuation.resume(document)
                    } else {
                        continuation.resume(null)
                    }
                }.addOnFailureListener { e ->
                    Platform().logger.loge("$tag::loadImpl($noteId) failed, error: $e")
                    continuation.resume(null)
                }
        }
    }

}
