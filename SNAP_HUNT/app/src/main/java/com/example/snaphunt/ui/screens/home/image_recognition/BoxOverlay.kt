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
        val modelInputSize = 448f

        val canvasRatio = size.width / size.height

        val scaledWidth: Float
        val scaledHeight: Float
        val xOffset: Float
        val yOffset: Float

        if (canvasRatio > 1f) {
            scaledWidth = modelInputSize
            scaledHeight = modelInputSize / canvasRatio
            xOffset = 0f
            yOffset = (modelInputSize - scaledHeight) / 2f
        } else {
            scaledWidth = modelInputSize * canvasRatio
            scaledHeight = modelInputSize
            xOffset = (modelInputSize - scaledWidth) / 2f
            yOffset = 0f
        }

        results.detections().forEach { detection ->
            val box = detection.boundingBox()

            val normLeft = (box.left - xOffset) / scaledWidth
            val normTop = (box.top - yOffset) / scaledHeight
            val normRight = (box.right - xOffset) / scaledWidth
            val normBottom = (box.bottom - yOffset) / scaledHeight

            val drawLeft = normLeft * size.width
            val drawTop = normTop * size.height
            val drawWidth = (normRight - normLeft) * size.width
            val drawHeight = (normBottom - normTop) * size.height

            drawRect(
                color = Color.Red,
                topLeft = Offset(x = drawLeft, y = drawTop),
                size = Size(width = drawWidth, height = drawHeight),
                style = Stroke(width = 4.dp.toPx())
            )
        }
    }
}