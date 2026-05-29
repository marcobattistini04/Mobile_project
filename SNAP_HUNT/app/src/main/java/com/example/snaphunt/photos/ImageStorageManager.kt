package com.example.snaphunt.photos

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import androidx.core.graphics.scale

class ImageStorageManager {

    // Salva un'immagine (ridotta o originale) in una cartella permanente e sicura dell'app
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