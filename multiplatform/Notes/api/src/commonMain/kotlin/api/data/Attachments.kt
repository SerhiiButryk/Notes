package api.data

import androidx.compose.runtime.Immutable
import api.data.Document.Companion.isUserFile

data class UserFile(
    val location: Any,
    val name: String,
)

@Immutable // To mark it stable for compose
data class Attachments(
    val files: List<UserFile> = emptyList()
) {
    fun hasAttachmentsFor(id: Long): Boolean {
        files.forEach {
            if (isUserFile(it.name, id)) {
                return true
            }
        }
        return false
    }
}
