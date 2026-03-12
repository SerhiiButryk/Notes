package com.notes.services.storage

import api.AppService.Companion.FIREBASE_STORAGE
import api.AppServices
import api.Platform
import api.data.AbstractStorageService
import api.data.Document
import api.data.toDocument
import api.data.toJson
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Service which implements database storage with Google Firestore server.
 *
 * Basic concepts of fire store:
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
 * Max storage is limited to 1 Gb
 */
class FirebaseFirestoreService(
    firestore: com.google.firebase.firestore.FirebaseFirestore
): AbstractStorageService() {

    private val tag = "FirebaseFirestore"
    private val database = firestore

    override val key = FIREBASE_STORAGE

    override var canUse: Boolean = false
        get() = isAuthenticated()

    private val COLLECTION_USERS = "users"
    private val COLLECTION_USER_NOTES = "user_notes"

    override suspend fun fetchAll(): List<Document> {
        Platform().logger.logi("$tag::fetchAll()")

        if (!isAuthenticated()) return emptyList()

        val authService = AppServices.getDefaultAuthService()
        val uid = authService.getUserId()

        return suspendCancellableCoroutine { continuation ->
            database
                .collection("$COLLECTION_USERS/$uid/$COLLECTION_USER_NOTES")
                .get()
                .addOnSuccessListener { snapshots ->
                    Platform().logger.logi("$tag::fetchAll() size = ${snapshots.size()}")
                    val list = mutableListOf<Document>()
                    for (snapshot in snapshots) {
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
                .document("$COLLECTION_USERS/$uid/$COLLECTION_USER_NOTES/${document.name}")
                .delete()
                .addOnSuccessListener {
                    Platform().logger.logi("$tag::delete(${document.name}) done")
                    continuation.resume(true)
                }.addOnFailureListener { e ->
                    Platform().logger.loge("$tag::delete(${document.name}) failed, error: $e")
                    e.printStackTrace()
                    continuation.resume(false)
                }
        }
    }

    override suspend fun store(document: Document): Boolean {

        if (!paramsCheck(document.name)) {
            return false
        }

        val authService = AppServices.getDefaultAuthService()
        val uid = authService.getUserId()

        return suspendCancellableCoroutine { continuation ->

            val payload = hashMapOf("content" to document.toJson())

            // Matches the location:
            // users/{userId}/user_notes/{noteId}

            database
                .document("$COLLECTION_USERS/$uid/$COLLECTION_USER_NOTES/${document.name}")
                .set(payload)
                .addOnSuccessListener {
                    Platform().logger.logi("$tag::store(${document.name}) done")
                    continuation.resume(true)
                }.addOnFailureListener { e ->
                    Platform().logger.loge("$tag::store(${document.name}) failed, error: $e")
                    continuation.resume(false)
                }
        }
    }

    override suspend fun selectDocName(initial: Long): Long? {
        Platform().logger.logi("$tag::selectDocName() trying to select a name for a new doc")
        var name = initial
        var doc = load(Document(name = name.toString()))
        var attempts = 1
        while (doc != null) {
            Platform().logger.loge("$tag::selectDocName() name '$name' is not available")
            if (attempts >= 100) {
                Platform().logger.loge("$tag::selectDocName() can't find a name")
                return null
            }
            name++
            attempts++
            // Retrying...
            doc = load(Document(name = name.toString()))
        }
        Platform().logger.logi("$tag::selectDocName() name '$name' is available")
        return name
    }

    override suspend fun load(document: Document): Document? {

        if (!paramsCheck(document.name)) {
            return null
        }

        val authService = AppServices.getDefaultAuthService()
        val uid = authService.getUserId()

        return suspendCancellableCoroutine { continuation ->

            val noteId = document.name

            // Get the location:
            // users/{userId}/user_notes/{noteId}

            database
                .document("$COLLECTION_USERS/$uid/$COLLECTION_USER_NOTES/$noteId")
                .get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot != null && snapshot.exists()) {
                        val document = snapshot.toString().toDocument()
                        Platform().logger.loge("$tag::load($noteId) done")
                        continuation.resume(document)
                    } else {
                        continuation.resume(null)
                    }
                }.addOnFailureListener { e ->
                    Platform().logger.loge("$tag::load($noteId) failed, error: $e")
                    continuation.resume(null)
                }
        }
    }

}