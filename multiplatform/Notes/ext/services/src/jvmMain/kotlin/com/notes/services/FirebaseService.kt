package com.notes.services

import api.AppService
import api.Platform
import com.google.firebase.FirebasePlatform
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize
import android.app.Application
import dev.gitlive.firebase.FirebaseApp

/**
 *  Firebase SDK service
 */
class FirebaseService : AppService {

    override val key = AppService.FIREBASE_MAIN

    private var firebaseApp: FirebaseApp? = null

    override fun onCreate() {

        try {

            // Initialize the required internal platform abstractions
            FirebasePlatform.initializeFirebasePlatform(object : FirebasePlatform() {

                val storage = mutableMapOf<String, String>()

                override fun store(key: String, value: String) {
                    storage[key] = value
                }

                override fun retrieve(key: String): String? {
                    return storage[key]
                }

                override fun clear(key: String) {
                    storage.remove(key)
                }

                override fun log(msg: String) {
                    Platform().logger.logi("[Firebase]: $msg")
                }
            })

            firebaseApp = Firebase.initialize(
                context = Application(),
                FirebaseOptions(
                    applicationId = "1:411307947225:android:a9136bc05f159711ed7ac8",
                    apiKey = "AIzaSyCOkpusQZDvNLpBWUJhUySeiaCopCnOez8"
                )
            )

            Platform().logger.logi("[Firebase]: init done")

        } catch (e: Exception) {
            Platform().logger.loge("[Firebase]: failed error = $e")
            e.printStackTrace()
        }

    }

    override suspend fun onDestroy() {
        firebaseApp?.delete()
    }

}