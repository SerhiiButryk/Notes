package com.notes.repo

import android.net.Uri
import api.Platform
import api.data.Attachments
import api.data.Image
import api.data.Notes
import api.data.getStringRep
import api.data.toNote
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
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

    fun saveImage(inputStream: InputStream, fileName: String) {
        Platform().logger.logi("saveImage:")

        val imageFolder = getOrCreateImageFolder()

        val file = File(imageFolder, fileName)

        try {
            FileOutputStream(file).use { outputStream ->
                inputStream.use { input ->
                    input.copyTo(outputStream)
                }
            }
            Platform().logger.logi("saveImage: done, name - '$fileName'")
        } catch (e: IOException) {
            Platform().logger.loge("saveImage: exception = $e")
            e.printStackTrace()
        }
    }

    fun getOrCreateImageFolder(): File {

        val rootDir = File(Platform().storage.getRootFilesDir())

        val imageFolder = File(rootDir, "img")

        if (!imageFolder.exists()) {
            val isCreated = imageFolder.mkdirs()
            if (!isCreated) {
                Platform().logger.loge("saveImage: failed to create subfolder")
            } else {
                Platform().logger.logi("saveImage: subfolder is created")
            }
        }

        return imageFolder
    }

    fun scanFolder(path: String): Attachments {
        val images = mutableListOf<Image>()
        val imgFolder = File(path)
        val files = imgFolder.listFiles()
        files?.forEach { f ->
            val uri = Uri.fromFile(f)
            images.add(Image(uri, f.name))
        }
        return Attachments(images)
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

    fun delete(image: Image) {
        Platform().logger.logi("delete: image deleting...")
        // Should not be null at this point
        val imgFolder = getOrCreateImageFolder()
        val fileToDelete = File(imgFolder, image.name)
        val result = fileToDelete.delete()
        Platform().logger.logi("delete: delete = '${result}' file '${image.name}'")
    }

    fun deleteAllFor(noteId: Long) {
        Platform().logger.logi("delete: all for $noteId")
        // Should not be null at this point
        val imgFolder = getOrCreateImageFolder()
        imgFolder.listFiles()?.forEach { file ->
            if (file.name.startsWith(noteId.toString()))
                file.delete()
        }
        Platform().logger.logi("delete: done")
    }

}