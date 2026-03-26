package api.data

import api.Platform
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

data class Document(val name: String, val data: String) {
    fun isEmpty() = name.isEmpty() && data.isEmpty()
}

fun Document.toJson(): String {
    val json = buildJsonObject {
        put("name", JsonPrimitive(name))
        put("content", JsonPrimitive(data))
    }
    return json.toString()
}

fun String.toDocument(): Document {
    val json = Json { ignoreUnknownKeys = true }
    val deserializedElement: JsonElement = try {
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