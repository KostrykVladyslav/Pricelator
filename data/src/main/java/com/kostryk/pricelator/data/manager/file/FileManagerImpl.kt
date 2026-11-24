package com.kostryk.pricelator.data.manager.file

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.kostryk.pricelator.domain.manager.file.FileManager
import java.io.File
import java.util.UUID


class FileManagerImpl(private val context: Context) : FileManager {

    override fun invoke(uri: Uri?): File? {
        if (uri == null) return null

        val contentResolver: ContentResolver? = context.contentResolver
        val mimeType: String? = contentResolver?.getType(uri)
        val mimeTypeMap = MimeTypeMap.getSingleton()
        val fileExtension = mimeTypeMap.getExtensionFromMimeType(mimeType)

        val inputStream = context.contentResolver.openInputStream(uri)
        return inputStream?.use { stream ->
            val bytes = stream.readBytes()
            val fileName = UUID.randomUUID().toString() + "." + (fileExtension ?: "bin")
            val tempFile = File(context.cacheDir, fileName)
            tempFile.writeBytes(bytes)
            tempFile
        }
    }
}