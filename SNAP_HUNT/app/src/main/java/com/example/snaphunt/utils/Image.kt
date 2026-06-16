package com.example.snaphunt.utils

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.graphics.createBitmap

fun uriToBitmap(imageUri: Uri, contentResolver: ContentResolver): Bitmap {
    return if (Build.VERSION.SDK_INT < 28) {
        @Suppress("DEPRECATION")
        MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
    } else {
        val source = ImageDecoder.createSource(contentResolver, imageUri)
        ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE

            decoder.isMutableRequired = true
        }
    }
}

fun prepareBitmapForModel(originalBitmap: Bitmap, targetSize: Int = 448): Bitmap {
    val outputBitmap = createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(outputBitmap)

    canvas.drawColor(Color.BLACK)

    val scale = targetSize.toFloat() / maxOf(originalBitmap.width, originalBitmap.height)
    val scaledWidth = (originalBitmap.width * scale).toInt()
    val scaledHeight = (originalBitmap.height * scale).toInt()

    val left = (targetSize - scaledWidth) / 2f
    val top = (targetSize - scaledHeight) / 2f

    val targetRect = Rect(left.toInt(), top.toInt(), left.toInt() + scaledWidth, top.toInt() + scaledHeight)

    val paint = android.graphics.Paint().apply {
        isFilterBitmap = true
        isAntiAlias = true
    }

    canvas.drawBitmap(originalBitmap, null, targetRect, paint)

    return outputBitmap
}