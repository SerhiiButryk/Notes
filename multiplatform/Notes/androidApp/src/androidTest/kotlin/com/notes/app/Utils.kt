package com.notes.app

import api.AppService
import api.AppServices
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


