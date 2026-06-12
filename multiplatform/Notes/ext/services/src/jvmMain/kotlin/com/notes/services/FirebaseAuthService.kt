package com.notes.services

import api.AppService.Companion.FIREBASE_AUTH
import api.Platform
import api.auth.AbstractAuthService
import api.auth.AuthResult
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

class FirebaseAuthService : AbstractAuthService() {

    private val tag = "FirebaseAuthService"

    override val key = FIREBASE_AUTH

    private val auth = Firebase.auth

    override suspend fun createUser(
        pass: String,
        email: String
    ): AuthResult {
        Platform().logger.logi("$tag::createUser()")
        return try {
            val result = auth.createUserWithEmailAndPassword(email, pass)
            if (result.user?.uid != null) {
                Platform().logger.logi("$tag::createUser() success")
                AuthResult.registrationSuccess(email)
            } else {
                Platform().logger.logi("$tag::createUser() failed")
                AuthResult.registrationFailed(email)
            }
        } catch (e: Exception) {
            Platform().logger.logi("$tag::createUser() failed error = $e")
            AuthResult.registrationFailed(email)
        }
    }

    override suspend fun login(
        pass: String,
        email: String,
        activityContext: Any?
    ): AuthResult {
        return try {
            val result = auth.signInWithEmailAndPassword(email, pass)
            if (result.user?.uid != null) {
                Platform().logger.logi("$tag::login() success")
                // Done
                callback?.onAuthCompleted(pass, getUserEmail())
                AuthResult.registrationSuccess(email)
            } else {
                Platform().logger.logi("$tag::login() failed")
                AuthResult.registrationFailed(email)
            }
        } catch (e: Exception) {
            Platform().logger.logi("$tag::login() failed error = $e")
            AuthResult.registrationFailed(email)
        }
    }

    override fun getUserEmail(): String = auth.currentUser?.email ?: ""

    override fun isAuthenticated(): Boolean = auth.currentUser?.uid != null

    override fun getUserId(): String {
        if (auth.currentUser?.uid == null) {
            return ""
        }
        return auth.currentUser?.uid!!
    }

    override suspend fun signOut(): Boolean {
        auth.signOut()
        return true
    }

}