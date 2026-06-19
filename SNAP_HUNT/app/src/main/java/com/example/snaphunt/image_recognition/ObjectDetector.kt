package com.example.snaphunt.image_recognition

import android.content.Context
import android.graphics.Bitmap
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetector
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetectorResult

class ObjectDetector(
    private val context: Context,
    private val modelPath: String = "efficientdet_lite2.tflite"
) {
    private var objectDetector: ObjectDetector? = null

    init {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath(modelPath)
            .setDelegate(Delegate.GPU)
            .build()

        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setBaseOptions(baseOptions)
            .setRunningMode(RunningMode.IMAGE)
            .setMaxResults(6)
            .setScoreThreshold(0.5f)
            .build()

        objectDetector = ObjectDetector.createFromOptions(context, options)
    }

    fun detect(bitmap: Bitmap): ObjectDetectorResult? {
        val mediaPipeImage = BitmapImageBuilder(bitmap).build()
        return objectDetector?.detect(mediaPipeImage)
    }

    fun close() {
        objectDetector?.close()
    }
}