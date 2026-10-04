package com.notes.app

import api.AppService
import api.AppServices
import api.auth.AbstractAuthService
import api.auth.AuthResult
import api.data.AbstractStorageService
import api.data.Document
import com.notes.repo.AndroidSyncManager
import com.notes.repo.AppRepository
import kotlinx.coroutines.CoroutineScope

suspend fun setupServices(
    setDelete: Boolean = true,
    setStore: Boolean = true,
    docsList: List<Document> = emptyList(),
) {
    val mockedStoreServiceGoogle = object : AbstractStorageService() {
        override val key: Any = AppService.GOOGLE_STORAGE

        override var canUse: Boolean = true

        override suspend fun store(document: Document): Boolean = setStore

        override suspend fun load(document: Document): Document = Document("", "")

        override suspend fun delete(document: Document): Boolean = setDelete

        override suspend fun fetchAll(): List<Document> = emptyList()
    }
    val mockedStoreServiceFirebase = object : AbstractStorageService() {
        override val key: Any = AppService.FIREBASE_STORAGE

        override var canUse: Boolean = true

        override suspend fun store(document: Document): Boolean = setStore

        override suspend fun load(document: Document): Document = Document("", "")

        override suspend fun delete(document: Document): Boolean = setDelete

        override suspend fun fetchAll(): List<Document> = docsList
    }
    val property = AppServices::class.java.getDeclaredField("appServices")
    property.isAccessible = true
    val list: MutableList<AppService> = property.get(AppServices) as MutableList<AppService>
    list.clear()
    list.add(mockedStoreServiceGoogle)
    list.add(mockedStoreServiceFirebase)
}

fun createAppRepo(
    syncManager: AndroidSyncManager,
    scope: CoroutineScope? = null,
): AppRepository {
    return AppRepository.create(syncManager, scope)
}

suspend fun createFakeAuthServices(
    emailOverride: String,
): AbstractAuthService {
    return object : AbstractAuthService() {

        override suspend fun createUser(
            pass: String,
            email: String
        ): AuthResult {
            return super.createUser(pass, email)
        }

        override suspend fun login(
            pass: String,
            email: String,
            activityContext: Any?
        ): AuthResult {
            return AuthResult.loginAccountChanged()
        }

        override suspend fun login(
            tokenId: String,
            activityContext: Any?
        ): AuthResult {
            return AuthResult.loginSuccess(emailOverride)
        }

        override suspend fun sendEmailVerify(): AuthResult {
            return super.sendEmailVerify()
        }

        override suspend fun verifyCode(code: String): Boolean {
            return super.verifyCode(code)
        }

        override suspend fun changePassword(newPass: String): Boolean {
            return super.changePassword(newPass)
        }

        override suspend fun isEmailVerified(): Boolean {
            return super.isEmailVerified()
        }

        override fun getUserEmail(): String {
            return emailOverride
        }

        override fun isAuthenticated(): Boolean {
            return true
        }

        override fun getUserId(): String {
            return super.getUserId()
        }

        override fun init(context: Any?) {
            super.init(context)
        }

        override suspend fun signOut(): Boolean {
            return true
        }

        override fun setAccountAutoselect(enable: Boolean) {
            super.setAccountAutoselect(enable)
        }

        override val key = AppService.FIREBASE_AUTH
    }
}


