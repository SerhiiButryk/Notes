package com.notes.services.storage

import api.PlatformAPIs
import api.StorageService
import api.data.Document

class EncryptedStore(private val service: FirebaseFirestore) : StorageService {

    override suspend fun store(document: Document): Boolean {
        val encrypted = PlatformAPIs.crypto.encryptWithDerivedKey(document.data)
        return service.store(document.name, encrypted)
    }

    override suspend fun load(name: String): Document? {
        val data = service.load(name)
        if (data != null) {
            val decrypted = PlatformAPIs.crypto.decryptWithDerivedKey(data)
            return Document(data = decrypted, name = name)
        }
        return null
    }

    override suspend fun delete(name: String): Boolean {
        return service.delete(name)
    }

    override suspend fun fetchAll(): List<Document> {
        val documents = service.fetchAll()
        return documents.map { doc ->
            val decrypted = PlatformAPIs.crypto.decryptWithDerivedKey(doc.data)
            Document(name = doc.name, data = decrypted)
        }
    }
}