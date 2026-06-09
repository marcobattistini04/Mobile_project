package com.example.snaphunt.photos

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import androidx.core.graphics.scale
import java.io.OutputStream

class ImageStorageManager {
    fun saveLocalImage(bitmap: Bitmap, context: Context): File {
        val directory = File(context.filesDir, "snap_photos")
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, "img_${UUID.randomUUID()}.jpg")

        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out) // 80% è il compromesso perfetto spazio/qualità
        }

        return file
    }

    fun saveImageToGallery(bitmap: Bitmap, context: Context): Boolean {
        val filename = "IMG_${UUID.randomUUID()}.jpg"

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/NomeTuaApp")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        return try {
            uri?.let {
                val out: OutputStream? = resolver.openOutputStream(it)
                out?.use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    resolver.update(it, contentValues, null, null)
                }
                true
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun createThumbnail(bitmap: Bitmap, maxSize: Int = 512): Bitmap {
        val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()

        val (w, h) = if (ratio > 1) {
            maxSize to (maxSize / ratio).toInt()
        } else {
            (maxSize * ratio).toInt() to maxSize
        }

        return bitmap.scale(w, h)
    }

    //funzione di utilità per eliminare i file locali una volta sincronizzati
    fun fileFromPath(path: String): File {
        return File(path)
    }
}