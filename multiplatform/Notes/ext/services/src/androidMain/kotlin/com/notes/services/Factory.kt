package com.notes.services

import api.auth.AbstractAuthService
import api.data.AbstractStorageService
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.notes.services.auth.FirebaseAuthService
import com.notes.services.storage.FirebaseFirestoreService

fun createFirebaseAuthService(): AbstractAuthService {
    return FirebaseAuthService(Firebase.auth)
}

fun createFirebaseFirestoreService(): AbstractStorageService {
    return FirebaseFirestoreService(Firebase.firestore)
}