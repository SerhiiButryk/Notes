package com.notes.repo

import api.Platform
import api.data.Notes
import api.data.getStringRep
import api.data.toNote
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader

class FilesManager {

    suspend fun saveToDisk(notes: List<Notes>): Boolean {
        val cacheDir = Platform().storage.getCacheDir()
        for (note in notes) {
            // A name of a file is note id
            val file = File(cacheDir, note.id.toString())
            Platform().logger.logi("saveToDisk: note = ${note.id}, file = ${file.name}")
            try {
                val payload = note.getStringRep()
                val cipherText = Platform().crypto.encrypt(payload)
                FileOutputStream(file).use { output ->
                    output.write(cipherText.toByteArray())
                }
            } catch (e: Exception) {
                Platform().logger.logi("saveToDisk: error: $e")
                e.printStackTrace()
                // Try to delete file
                file.delete()
                // Say that we've failed
                return false
            }
        }
        Platform().logger.logi("saveToDisk: done")
        return true
    }

    suspend fun readFromDisk(): List<Notes> {

        val cacheDir = Platform().storage.getCacheDir()

        val notes = mutableListOf<Notes>()

        val files = File(cacheDir).listFiles()

        if (files == null) {
            Platform().logger.logi("readFromDisk: no files")
            return notes
        }

        for (file in files) {

            if (file == null) continue

            if (file.isFile) {

                val content: String? = try {
                    FileInputStream(file).use { input ->
                        BufferedReader(InputStreamReader(input)).use { reader ->
                            reader.readText()
                        }
                    }
                } catch (e: Exception) {
                    Platform().logger.loge("readFromDisk: error $e reading a file: ${file.name}")
                    e.printStackTrace()
                    null
                }

                content?.apply {
                    val plainText = Platform().crypto.decrypt(this)
                    val id = file.name.toLong()
                    val note = plainText.toNote(id)
                    notes.add(note)
                }

            }
        }

        Platform().logger.logi("readFromDisk: size = ${notes.size}")

        return notes
    }

    suspend fun clearCache() {
        val cacheDir = Platform().storage.getCacheDir()
        val files = File(cacheDir).listFiles()
        if (files != null) {
            files.forEach { file ->
                if (file.isFile) {
                    file.delete()
                }
            }
        }
        Platform().logger.logi("clearCache: cache has been cleared up")
    }

}