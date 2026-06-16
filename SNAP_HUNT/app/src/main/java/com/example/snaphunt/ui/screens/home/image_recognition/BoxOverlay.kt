package com.example.snaphunt.ui.screens.home.image_recognition

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetectorResult

@Composable
fun BoxOverlay(
    results: ObjectDetectorResult,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val modelInputSize = 448f // Il tuo targetSize

        results.detections().forEach { detection ->
            val box = detection.boundingBox()

            // 1. Convertiamo le coordinate 448x448 in proporzioni (0.0 - 1.0)
            val left = box.left / modelInputSize
            val top = box.top / modelInputSize
            val right = box.right / modelInputSize
            val bottom = box.bottom / modelInputSize

            // 2. Mappiamo le proporzioni sulle dimensioni reali del tuo componente UI
            // Poiché il box ha aspectRatio(1f), size.width e size.height sono uguali
            drawRect(
                color = Color.Red,
                topLeft = Offset(
                    x = left * size.width,
                    y = top * size.height
                ),
                size = Size(
                    width = (right - left) * size.width,
                    height = (bottom - top) * size.height
                ),
                style = Stroke(width = 4.dp.toPx())
            )
        }
    }
}