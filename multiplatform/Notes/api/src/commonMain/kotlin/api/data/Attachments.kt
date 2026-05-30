package api.data

import androidx.compose.runtime.Immutable
import api.Platform

data class Image(
    val location: Any,
    val name: String,
)

@Immutable // To mark it stable for compose
data class Attachments(
    val images: List<Image> = emptyList()
) {
    fun hasAttachmentsFor(noteId: Long): Boolean {
        images.forEach {
            if (it.name.startsWith(noteId.toString())) {
                return true
            }
        }
        return false
    }
}
