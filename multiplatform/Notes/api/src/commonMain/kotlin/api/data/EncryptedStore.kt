package api.data

import api.Platform

/**
 * Service which adds a layer of encryption on storage service
 */
class EncryptedStore(val delegate: AbstractStorageService) : AbstractStorageService() {

    override val key = delegate.key

    override var canUse: Boolean = delegate.canUse
        get() = delegate.canUse

    override suspend fun store(document: Document): Boolean {
        // Do not encrypt, hope it doesn't have sensitive information
        if (document.isFile) {
            return delegate.store(document)
        }
        val encrypted = Platform().crypto.encryptWithDerivedKey(document.data)
        return delegate.store(Document(document.name, encrypted))
    }

    override suspend fun load(document: Document): Document? {
        val document = delegate.load(document)
        if (document != null) {
            val decrypted = Platform().crypto.decryptWithDerivedKey(document.data)
            return Document(data = decrypted, name = document.name)
        }
        return null
    }

    override suspend fun delete(document: Document): Boolean {
        return delegate.delete(document)
    }

    override suspend fun fetchAll(): List<Document> {
        val documents = delegate.fetchAll()
        return documents.map { doc ->
            val decrypted = Platform().crypto.decryptWithDerivedKey(doc.data)
            Document(name = doc.name, data = decrypted)
        }
    }
}