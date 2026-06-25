package com.notes.repo

import android.net.Uri
import api.Platform
import api.data.Attachments
import api.data.Notes
import api.data.UserFile
import api.data.getStringRep
import api.data.toNote
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream

private const val tag = "FilesManager"

class FilesManager {

    suspend fun cacheNotes(notes: List<Notes>): Boolean {
        val cacheDir = Platform().storage.getCacheDir()
        for (note in notes) {
            // A name of a file is note id
            val file = File(cacheDir, note.id.toString())
            Platform().logger.logi("$tag::cacheNotes: note = ${note.id}, file = ${file.name}")
            try {
                val payload = note.getStringRep()
                val cipherText = Platform().crypto.encrypt(payload)
                FileOutputStream(file).use { output ->
                    output.write(cipherText.toByteArray())
                }
            } catch (e: Exception) {
                Platform().logger.logi("$tag::cacheNotes: error: $e")
                e.printStackTrace()
                // Try to delete file
                file.delete()
                // Say that we've failed
                return false
            }
        }
        Platform().logger.logi("$tag::cacheNotes: done")
        return true
    }

    suspend fun readCache(): List<Notes> {

        val cacheDir = Platform().storage.getCacheDir()

        val notes = mutableListOf<Notes>()

        val files = File(cacheDir).listFiles()

        if (files == null) {
            Platform().logger.logi("$tag::readCache: no files")
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
                    Platform().logger.loge("$tag::readCache: error $e reading a file: ${file.name}")
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

        Platform().logger.logi("$tag::readCache: size = ${notes.size}")

        return notes
    }

    fun saveImage(inputStream: InputStream, fileName: String): File? {
        Platform().logger.logi("$tag::saveImage:")

        val imageFolder = getOrCreateImageFolder()

        val file = File(imageFolder, fileName)

        try {
            FileOutputStream(file).use { outputStream ->
                inputStream.use { input ->
                    input.copyTo(outputStream)
                }
            }
            Platform().logger.logi("$tag::saveImage: done, name - '$fileName'")
            return file
        } catch (e: IOException) {
            Platform().logger.loge("$tag::saveImage: exception = $e")
            e.printStackTrace()
        }
        return null
    }

    fun getOutputStreamForImage(fileName: String): OutputStream {
        Platform().logger.logi("$tag::getOutputStreamForImage:")
        val imageFolder = getOrCreateImageFolder()
        val file = File(imageFolder, fileName)
        return FileOutputStream(file)
    }

    fun getOrCreateImageFolder(): File {

        val rootDir = File(Platform().storage.getRootFilesDir())

        val imageFolder = File(rootDir, "img")

        if (!imageFolder.exists()) {
            val isCreated = imageFolder.mkdirs()
            if (!isCreated) {
                Platform().logger.loge("$tag::saveImage: failed to create subfolder")
            } else {
                Platform().logger.logi("$tag::saveImage: subfolder is created")
            }
        }

        return imageFolder
    }

    fun printFolderInfo() {
        Platform().logger.logi("$tag::printFolderInfo:")
        val rootDir = getOrCreateImageFolder()
        rootDir.listFiles()?.forEach { file ->
            Platform().logger.logi("$tag::printFolderInfo: file = ${file.name}, size = ${file.length()}")
        }
    }

    fun scanFolder(path: String): Attachments {
        val images = mutableListOf<UserFile>()
        val imgFolder = File(path)
        val files = imgFolder.listFiles()
        files?.forEach { f ->
            val uri = Uri.fromFile(f)
            images.add(UserFile(uri, f.name))
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
        Platform().logger.logi("$tag::clearCache: cache has been cleared up")
    }

    fun delete(file: UserFile) {
        Platform().logger.logi("$tag::delete: deleting...")
        // Should not be null at this point
        val imgFolder = getOrCreateImageFolder()
        val fileToDelete = File(imgFolder, file.name)
        val result = fileToDelete.delete()
        Platform().logger.logi("$tag::delete: delete = '${result}' file '${file.name}'")
    }

    fun deleteAllFor(noteId: Long) {
        Platform().logger.logi("$tag::delete: all for $noteId")
        // Should not be null at this point
        val imgFolder = getOrCreateImageFolder()
        imgFolder.listFiles()?.forEach { file ->
            if (file.name.startsWith(noteId.toString()))
                file.delete()
        }
        Platform().logger.logi("$tag::delete: done")
    }

    fun hasFile(name: String): Boolean {
        val files = getOrCreateImageFolder().listFiles()
        if (files != null) {
            files.forEach { file ->
                if (file.name == name) {
                    return true
                }
            }
        }
        return false
    }

}