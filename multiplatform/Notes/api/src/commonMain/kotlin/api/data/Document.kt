package api.data

import api.Platform
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File

data class Document(
    val name: String = "",
    val data: String = "",
) {
    /**
     * Second constructor
     */
    constructor(file: File, override: Boolean = false) : this(file.name, "") {
        this.file = file
        this.isFile = true
        this.override = override
    }

    var file: File? = null
    var override: Boolean = false

    var isFile: Boolean = false

    fun isEmpty() = name.isEmpty() && data.isEmpty()

    companion object {
        // File name follows specific pattern to filter data fast
        fun createFileName(
            id: Long,
            name: String,
        ) = "${getFilePrefix(id)}_img_$name"

        private fun getUserFilePrefix() = "userfile"

        private fun getFilePrefix(id: Long) = "${getUserFilePrefix()}_$id"

        fun isUserFile(
            name: String,
            id: Long,
        ): Boolean = name.startsWith(getFilePrefix(id))
    }
}

fun Document.toJson(): String {
    val json =
        buildJsonObject {
            put("name", JsonPrimitive(name))
            put("content", JsonPrimitive(data))
        }
    return json.toString()
}

fun String.toDocument(): Document {
    val json = Json { ignoreUnknownKeys = true }
    val deserializedElement: JsonElement =
        try {
            json.parseToJsonElement(this)
        } catch (e: Exception) {
            Platform().logger.loge("toDocument(): error = $e")
            return Document("", "")
        }
    try {
        val name = deserializedElement.jsonObject["name"]?.jsonPrimitive?.content ?: ""
        val data = deserializedElement.jsonObject["content"]?.jsonPrimitive?.content ?: ""
        return Document(name, data)
    } catch (e: IllegalArgumentException) {
        Platform().logger.loge("toDocument(): error = $e")
        return Document("", "")
    }
}
