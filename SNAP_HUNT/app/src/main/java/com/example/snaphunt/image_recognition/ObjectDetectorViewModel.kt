package com.example.snaphunt.image_recognition

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snaphunt.data.repositories.points_multiplier.PointsMultiplierRepository
import com.example.snaphunt.utils.prepareBitmapForModel
import com.example.snaphunt.utils.uriToBitmap
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetectorResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.String

data class DetectionResults(
    val aiLabel: String = "",
    val aiConfidence: Double = 0.0,
    val success: Boolean = false,
    val points: Int = 0,
    val additionalObjects: Int = 0
)
class ObjectDetectionViewModel(
    private val detector: ObjectDetector,
    private val pointsMultiplierRepository: PointsMultiplierRepository
) : ViewModel() {

    private val _detectionResults = MutableStateFlow<DetectionResults?>(null)
    val detectionResults = _detectionResults.asStateFlow()

    private val _rawDetectionResult = MutableStateFlow<ObjectDetectorResult?>(null)
    val rawDetectionResult = _rawDetectionResult.asStateFlow()

    val pointsMultiplier: StateFlow<Int> = pointsMultiplierRepository.weeklyMultiplier
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 1
        )

    val dailyBonusBase = 10

    fun detect(bitmap: Bitmap): ObjectDetectorResult? {
        return detector.detect(bitmap)
    }

    override fun onCleared() {
        super.onCleared()
        detector.close()
    }

    private fun processImage(bitmap: Bitmap, challenge: DailyObjects) {
        viewModelScope.launch(Dispatchers.Default) {
            val preparedBitMap = prepareBitmapForModel(bitmap)
            try {
                val results = detect(preparedBitMap)
                processResults(challenge, results!!)
            } finally {
                preparedBitMap.recycle()
            }
        }
    }

    fun processImageFromUri(uri: Uri, contentResolver: ContentResolver, challenge: DailyObjects) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bitmap = uriToBitmap(uri, contentResolver)

                if (bitmap == null || bitmap.isRecycled) {
                    return@launch
                }

                val finalBitmap = if (bitmap.config != Bitmap.Config.ARGB_8888) {
                    bitmap.copy(Bitmap.Config.ARGB_8888, false)
                } else {
                    bitmap
                }

                processImage(finalBitmap, challenge)

            } catch (e: Exception) { }
        }
    }

    private fun processResults(
        challenge: DailyObjects,
        modelResults: ObjectDetectorResult
    ) {

        val match = modelResults.detections().find { detection ->
            detection.categories().any {
                it.categoryName().equals(challenge.keyword, ignoreCase = true)
            }
        }

        val success = match != null
        var aiLabel = ""
        var aiConfidence = 0.0
        var points = 0
        var additionalObjects = 0

        if (success) {
            val bestCategory = match.categories().maxByOrNull { it.score() }
            aiLabel = bestCategory?.categoryName() ?: "unknown"
            aiConfidence = bestCategory?.score()?.toDouble() ?: 0.0

            val totalObjects = modelResults.detections().size
            additionalObjects = (totalObjects - 1).coerceAtLeast(0)

            val basePoints = 50.0
            val confidenceBonus = aiConfidence * 20.0
            val extraObjectsBonus = additionalObjects * 10.0

            points = (basePoints + confidenceBonus + extraObjectsBonus + (pointsMultiplier.value * dailyBonusBase)).toInt()
        }

        val attempt = DetectionResults(
            aiLabel = aiLabel,
            aiConfidence = aiConfidence,
            success = success,
            points = points,
            additionalObjects = additionalObjects
        )

        _rawDetectionResult.value = modelResults
        _detectionResults.value = attempt
    }

    fun clearResults() {
        _detectionResults.value = null
        _rawDetectionResult.value = null
    }
}