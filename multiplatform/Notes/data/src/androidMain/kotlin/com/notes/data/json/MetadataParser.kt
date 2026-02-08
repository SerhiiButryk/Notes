package com.notes.data.json

import api.PlatformAPIs.logger
import api.data.AbstractStorageService
import com.notes.data.NotesMetadataEntity
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * TODO: Improve in future. Can be replaced with a wrapper or proxy class.
 */

fun NotesMetadataEntity.isPendingDeletionOnRemote(): Boolean {
    try {
        val jsonElement = parseJson(metadata) ?: return false
        val pendingFirebase =
            jsonElement.jsonObject["pending_delete_on_firebase"]?.jsonPrimitive?.content ?: ""
        val pendingGoogle =
            jsonElement.jsonObject["pending_delete_on_googledrive"]?.jsonPrimitive?.content ?: ""
        return pendingFirebase.toBoolean() || pendingGoogle.toBoolean()
    } catch (e: IllegalArgumentException) {
        logger.loge("NotesMetadataEntity.isPendingDeletionOnRemote(): error = $e")
        return false
    }
}

fun NotesMetadataEntity.isPendingUpdateOnRemote(): Boolean {
    try {
        val jsonElement = parseJson(metadata) ?: return false
        val pendingFirebase =
            jsonElement.jsonObject["pending_update_on_firebase"]?.jsonPrimitive?.content ?: ""
        val pendingGoogle =
            jsonElement.jsonObject["pending_update_on_googledrive"]?.jsonPrimitive?.content ?: ""
        return pendingFirebase.toBoolean() || pendingGoogle.toBoolean()
    } catch (e: IllegalArgumentException) {
        logger.loge("NotesMetadataEntity.isPendingUpdateOnRemote(): error = $e")
        return false
    }
}

fun NotesMetadataEntity.updateForDatastore(
    dataStore: AbstractStorageService,
    pendingDelete: Boolean? = null,
    pendingUpdate: Boolean? = null,
): String {
    if (dataStore.name == "firebase") {
        return update(
            pendingDeleteFirebase = pendingDelete,
            pendingUpdateFirebase = pendingUpdate,
        )
    }
    if (dataStore.name == "googledrive") {
        return update(
            pendingDeleteGoogle = pendingDelete,
            pendingUpdateGoogle = pendingUpdate,
        )
    }
    throw IllegalArgumentException("Unknown service")
}

fun NotesMetadataEntity.update(
    pendingDelete: Boolean? = null,
    pendingUpdate: Boolean? = null,
): String {
    return update(
        pendingDeleteFirebase = pendingDelete,
        pendingDeleteGoogle = pendingDelete,
        pendingUpdateFirebase = pendingUpdate,
        pendingUpdateGoogle = pendingUpdate,
    )
}

fun NotesMetadataEntity.update(
    pendingDeleteFirebase: Boolean? = null,
    pendingDeleteGoogle: Boolean? = null,
    pendingUpdateFirebase: Boolean? = null,
    pendingUpdateGoogle: Boolean? = null,
): String {

    val jsonElement = parseJson(metadata)

    var pendingDeleteFirebaseValue: Boolean? = pendingDeleteFirebase
    var pendingDeleteGoogleValue: Boolean? = pendingDeleteGoogle
    var pendingUpdateFirebaseValue: Boolean? = pendingUpdateFirebase
    var pendingUpdateGoogleValue: Boolean? = pendingUpdateGoogle

    try {
        if (pendingDeleteFirebaseValue == null) {
            val str = jsonElement?.jsonObject["pending_delete_on_firebase"]?.jsonPrimitive?.content
                ?: "false"
            pendingDeleteFirebaseValue = str.toBoolean()
        }

        if (pendingDeleteGoogleValue == null) {
            val str =
                jsonElement?.jsonObject["pending_delete_on_googledrive"]?.jsonPrimitive?.content
                    ?: "false"
            pendingDeleteGoogleValue = str.toBoolean()
        }

        if (pendingUpdateFirebaseValue == null) {
            val str = jsonElement?.jsonObject["pending_update_on_firebase"]?.jsonPrimitive?.content
                ?: "false"
            pendingUpdateFirebaseValue = str.toBoolean()
        }

        if (pendingUpdateGoogleValue == null) {
            val str =
                jsonElement?.jsonObject["pending_update_on_googledrive"]?.jsonPrimitive?.content
                    ?: "false"
            pendingUpdateGoogleValue = str.toBoolean()
        }
    } catch (e: Exception) {
        logger.loge("NotesMetadataEntity.update(): error = $e")
    }

    return toJson(
        pendingDeleteFirebase = pendingDeleteFirebaseValue!!,
        pendingDeleteGoogle = pendingDeleteGoogleValue!!,
        pendingUpdateFirebase = pendingUpdateFirebaseValue!!,
        pendingUpdateGoogle = pendingUpdateGoogleValue!!
    )
}

fun toJson(
    pendingDeleteFirebase: Boolean,
    pendingDeleteGoogle: Boolean,
    pendingUpdateFirebase: Boolean,
    pendingUpdateGoogle: Boolean,
): String {
    val json = buildJsonObject {
        put("pending_delete_on_firebase", JsonPrimitive(pendingDeleteFirebase))
        put("pending_delete_on_googledrive", JsonPrimitive(pendingDeleteGoogle))
        put("pending_update_on_firebase", JsonPrimitive(pendingUpdateFirebase))
        put("pending_update_on_googledrive", JsonPrimitive(pendingUpdateGoogle))
    }
    return json.toString()
}

private fun parseJson(metadata: String): JsonElement? {
    val json = Json { ignoreUnknownKeys = true }
    val deserializedElement: JsonElement = try {
        json.parseToJsonElement(metadata)
    } catch (e: Exception) {
        logger.loge("NotesMetadataEntity.parseJson(): error = $e")
        return null
    }
    return deserializedElement
}