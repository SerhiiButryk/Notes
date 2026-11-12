package api.data

import androidx.compose.runtime.Immutable
import api.data.Document.Companion.isUserFile
import java.io.File

data class UserFile(
    val file: File,
)

@Immutable // To mark it stable for compose
data class Attachments(
    val files: List<UserFile> = emptyList(),
) {
    fun hasAttachmentsFor(id: Long): Boolean {
        files.forEach {
            if (isUserFile(it.file.name, id)) {
                return true
            }
        }
        return false
    }
}
