package com.notes.services.storage

import api.AppServices
import api.PlatformAPIs.logger
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
 * Google defines 'collection' and 'document'. 'Collection' is like a folder,
 * and 'document' is like a database file which actually stores data.
 * We can access a document using a path like 'users/{userId}/user_notes/{noteId}'.
 *
 * Permissions can be controlled by special rules on firebase console.
 */

class FirebaseFirestore : AbstractStorageService() {

    private val tag = "FirebaseFirestore"
    private val database = Firebase.firestore

    override val name: String = "firebase"

    override var canUse: Boolean = false
        get() = isAuthenticated()

    override suspend fun store(document: Document): Boolean = storeImpl(document)

    override suspend fun load(name: String): Document? = loadImpl(name)

    override suspend fun fetchAll(): List<Document> {
        logger.logi("$tag::fetchAll()")

        if (!isAuthenticated()) return emptyList()

        val authService = AppServices.getDefaultAuthService()!!
        val uid = authService.getUserId()

        return suspendCancellableCoroutine { continuation ->
            database
                .collection("users/$uid/user_notes")
                .get()
                .addOnSuccessListener { snapshots ->
                    logger.logi("$tag::fetchAll() sz = ${snapshots.size()}")
                    val list = mutableListOf<Document>()
                    for (snapshot in snapshots) {
                        logger.logi("$tag::fetchAll() <= ${snapshot.id}")
                        val json = snapshot.data["content"] as? String ?: ""
                        val doc = json.toDocument()
                        if (!doc.isEmpty()) {
                            list.add(doc)
                        }
                    }
                    continuation.resume(list)
                }.addOnFailureListener { e ->
                    logger.loge("$tag::fetchAll() error: $e")
                    continuation.resume(emptyList())
                }
        }
    }

    override suspend fun delete(name: String): Boolean {
        logger.logi("$tag::delete()")

        if (!paramsCheck(name)) {
            return false
        }

        val authService = AppServices.getDefaultAuthService()!!
        val uid = authService.getUserId()

        return suspendCancellableCoroutine { continuation ->
            database
                .document("users/$uid/user_notes/$name")
                .delete()
                .addOnSuccessListener {
                    logger.logi("$tag::delete() success, deleted name = $name")
                    continuation.resume(true)
                }.addOnFailureListener { e ->
                    logger.loge("$tag::delete() failed to delete name = $name")
                    continuation.resume(false)
                }
        }
    }

    private suspend fun storeImpl(
        document: Document
    ): Boolean {
        val authService = AppServices.getDefaultAuthService()!!
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
                    logger.logi("$tag::storeImpl() => '${document.name}'")
                    continuation.resume(true)
                }.addOnFailureListener { e ->
                    logger.loge("$tag::storeImpl() failed, error: $e")
                    continuation.resume(false)
                }
        }
    }

    private suspend fun loadImpl(name: String): Document? {
        val authService = AppServices.getDefaultAuthService()!!
        val uid = authService.getUserId()

        if (!paramsCheck(name)) {
            return null
        }

        return suspendCancellableCoroutine { continuation ->

            val noteId = name

            // Get the location:
            // users/{userId}/user_notes/{noteId}

            database
                .document("users/$uid/user_notes/$noteId")
                .get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot != null && snapshot.exists()) {
                        val document = snapshot.toString().toDocument()
                        logger.loge("$tag::loadImpl() <= ${document.name}")
                        continuation.resume(document)
                    } else {
                        continuation.resume(null)
                    }
                }.addOnFailureListener { e ->
                    logger.loge("$tag::loadImpl() failed, error: $e")
                    continuation.resume(null)
                }
        }
    }

}
