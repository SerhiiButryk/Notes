package com.notes.services

import android.app.Application
import api.AppService
import api.Platform
import api.auth.AbstractAuthService
import api.data.AbstractStorageService
import com.google.firebase.FirebasePlatform
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.notes.services.auth.FirebaseAuthService
import com.notes.services.storage.FirebaseFirestoreService
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.android
import dev.gitlive.firebase.initialize

/**
 *  Firebase SDK integration
 */
class Firebase : AppService {

    private val tag = "Firebase"

    override val key = AppService.FIREBASE_MAIN

    var firebaseApp: FirebaseApp? = null

    fun createAuthService(): AbstractAuthService {
        val android = firebaseApp!!.android
        val auth = FirebaseAuth.getInstance(android)
        return FirebaseAuthService(auth)
    }

    fun createFirestoreService(): AbstractStorageService {
        val android = firebaseApp!!.android
        val firestore = FirebaseFirestore.getInstance(android)
        return FirebaseFirestoreService(firestore)
    }

    override fun onCreate() {
        try {
            // Initialize the required internal platform abstractions
            FirebasePlatform.initializeFirebasePlatform(
                object : FirebasePlatform() {
                    val storage = mutableMapOf<String, String>()

                    override fun store(
                        key: String,
                        value: String,
                    ) {
                        storage[key] = value
                    }

                    override fun retrieve(key: String): String? = storage[key]

                    override fun clear(key: String) {
                        storage.remove(key)
                    }

                    override fun log(msg: String) {
                        // Don't show by default as it usually doesn't help
                        if (false) {
                            Platform().logger.logd("$tag: $msg")
                        }
                    }
                },
            )

            firebaseApp =
                Firebase.initialize(
                    context = Application(),
                    FirebaseOptions(
                        // TODO Load json instead for example
                        applicationId = "1:411307947225:android:a9136bc05f159711ed7ac8",
                        apiKey = "AIzaSyCOkpusQZDvNLpBWUJhUySeiaCopCnOez8",
                        projectId = "fancynotesdevtest"
                    ),
                )

            Platform().logger.logi("$tag: init done")
        } catch (e: Exception) {
            Platform().logger.loge("$tag: failed error = $e")
            e.printStackTrace()
        }
    }

    override suspend fun onDestroy() {
        firebaseApp?.delete()
        Platform().logger.logi("$tag: onDestroy")
    }
}
