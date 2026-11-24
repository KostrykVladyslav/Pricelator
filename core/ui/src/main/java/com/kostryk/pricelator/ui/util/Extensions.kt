package com.kostryk.pricelator.ui.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.compose.runtime.saveable.Saver
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun createBinFileWithTwoArg(
    context: Context,
    fileName: String = "header.bin",
    firstArg: String,
    secondArg: String
): File {
    val vBytes = firstArg.toByteArray(Charsets.UTF_8)
    val tBytes = secondArg.toByteArray(Charsets.UTF_8)

    val buffer = ByteBuffer
        .allocate(4 + vBytes.size + 4 + tBytes.size)
        .order(ByteOrder.LITTLE_ENDIAN)

    buffer.putInt(vBytes.size)
    buffer.put(vBytes)
    buffer.putInt(tBytes.size)
    buffer.put(tBytes)

    val file = File(context.cacheDir, fileName)
    file.writeBytes(buffer.array())
    return file
}

fun createBinWithOneArg(context: Context, crc: String): File {
    val cBytes = crc.toByteArray(Charsets.UTF_8)

    val buffer = ByteBuffer
        .allocate(4 + cBytes.size)
        .order(ByteOrder.LITTLE_ENDIAN)
        .apply {
            putInt(cBytes.size)
            put(cBytes)
        }

    val file = File(context.cacheDir, "end.bin")
    file.writeBytes(buffer.array())
    return file
}

fun createBinWithThreeArg(
    context: Context,
    fileName: String = "application.bin",
    firstArg: String,
    secondArg: String,
    thirdArg: String
): File {
    val vBytes = firstArg.toByteArray(Charsets.UTF_8)
    val cBytes = secondArg.toByteArray(Charsets.UTF_8)
    val pBytes = thirdArg.toByteArray(Charsets.UTF_8)

    val totalSize = 4 + vBytes.size +
            4 + cBytes.size +
            4 + pBytes.size

    val buffer = ByteBuffer
        .allocate(totalSize)
        .order(ByteOrder.LITTLE_ENDIAN)
        .apply {
            putInt(vBytes.size)
            put(vBytes)
            putInt(cBytes.size)
            put(cBytes)
            putInt(pBytes.size)
            put(pBytes)
        }

    val file = File(context.cacheDir, fileName)
    file.writeBytes(buffer.array())
    return file
}

fun shareBin(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/octet-stream"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share .bin file"))
}

fun shareBinWithText(context: Context, file: File, text: String) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/octet-stream"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_TEXT, text)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(
        Intent.createChooser(intent, "Share .bin file")
    )
}

fun Context.showMessage(message: String) {
    Toast.makeText(
        this,
        message,
        Toast.LENGTH_SHORT
    ).show()
}


fun Context.exportToDownloads(bytes: ByteArray): Boolean {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = contentResolver
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, "hex_content.bin")
                put(MediaStore.Downloads.MIME_TYPE, "application/octet-stream")
                put(MediaStore.Downloads.IS_PENDING, 1)
            }

            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                ?: return false

            resolver.openOutputStream(uri, "w")?.use { it.write(bytes) }
            values.clear()
            values.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
            true
        } else {
            @Suppress("DEPRECATION")
            val path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!path.exists()) path.mkdirs()
            File(path, "hex_content.bin").writeBytes(bytes)
            true
        }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun copyUriToCache(context: Context, uri: Uri, fileName: String): File {
    val input = context.contentResolver.openInputStream(uri)!!
    val outFile = File(context.cacheDir, fileName)
    FileOutputStream(outFile).use { out -> input.use { it.copyTo(out) } }
    return outFile
}

fun Uri.copyToCache(context: Context, name: String): File {
    val dst = File(context.cacheDir, name)
    context.contentResolver.openInputStream(this)!!.use { src ->
        FileOutputStream(dst).use { dstOut -> src.copyTo(dstOut) }
    }
    return dst
}

val ByteArraySaver = Saver<ByteArray?, String>(
    save = { bytes ->
        bytes?.let {
            Base64.encodeToString(
                it,
                Base64.NO_WRAP or Base64.URL_SAFE
            )
        } ?: ""
    },
    restore = { str ->
        if (str.isNotEmpty())
            Base64.decode(
                str,
                Base64.NO_WRAP or Base64.URL_SAFE
            )
        else null
    }
)

fun shareBootloaderBinFile(
    context: Context,
    version: String,
    address: String,
    content: ByteArray
) {
    val verBytes = version.encodeToByteArray()
    require(verBytes.size <= 255) { "Version is too long (max 255 bytes)" }

    val addrInt = address.removePrefix("0x").toUInt(16).toInt()
    val bootBinBytes = ByteBuffer
        .allocate(1 + verBytes.size + 4 + 4 + content.size)
        .order(ByteOrder.BIG_ENDIAN)
        .apply {
            put(verBytes.size.toByte())
            put(verBytes)
            putInt(addrInt)
            putInt(content.size)
            put(content)
        }
        .array()

    val file = File.createTempFile(
        "boot_${version}_",
        ".bin",
        context.cacheDir
    ).apply { writeBytes(bootBinBytes) }

    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )

    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/octet-stream"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    val resInfo = context.packageManager
        .queryIntentActivities(sendIntent, PackageManager.MATCH_DEFAULT_ONLY)
    resInfo.forEach {
        context.grantUriPermission(
            it.activityInfo.packageName,
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }

    context.startActivity(Intent.createChooser(sendIntent, "Share .bin file"))
}