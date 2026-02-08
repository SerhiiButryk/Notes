package api.data

import api.PlatformAPIs

/**
 * Service which adds a layer of encryption on storage service
 */
class EncryptedStore(val delegate: AbstractStorageService) : AbstractStorageService() {

    override val name: String = delegate.name

    override var canUse: Boolean = delegate.canUse
        get() = delegate.canUse

    override suspend fun store(document: Document): Boolean {
        val encrypted = PlatformAPIs.crypto.encryptWithDerivedKey(document.data)
        return delegate.store(Document(document.name, encrypted))
    }

    override suspend fun load(name: String): Document? {
        val data = delegate.load(name)
        if (data != null) {
            val decrypted = PlatformAPIs.crypto.decryptWithDerivedKey(data.data)
            return Document(data = decrypted, name = name)
        }
        return null
    }

    override suspend fun delete(name: String): Boolean {
        return delegate.delete(name)
    }

    override suspend fun fetchAll(): List<Document> {
        val documents = delegate.fetchAll()
        return documents.map { doc ->
            val decrypted = PlatformAPIs.crypto.decryptWithDerivedKey(doc.data)
            Document(name = doc.name, data = decrypted)
        }
    }
}