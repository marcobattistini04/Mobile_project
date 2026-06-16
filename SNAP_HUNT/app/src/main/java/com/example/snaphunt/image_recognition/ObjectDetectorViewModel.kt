package com.example.snaphunt.image_recognition

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snaphunt.utils.prepareBitmapForModel
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetectorResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ObjectDetectionViewModel(private val detector: ObjectDetector) : ViewModel() {

    private val _detectionResults = MutableStateFlow<ObjectDetectorResult?>(null)
    val detectionResults = _detectionResults.asStateFlow()
    fun detect(bitmap: Bitmap): ObjectDetectorResult? {
        return detector.detect(bitmap)
    }

    override fun onCleared() {
        super.onCleared()
        detector.close()
    }

    fun processImage(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.Default) {
            val preparedBitMap = prepareBitmapForModel(bitmap)
            try {
                val results = detect(preparedBitMap)
                _detectionResults.value = results
            } finally {
                preparedBitMap.recycle()
            }
        }
    }

    fun clearResults() {
        _detectionResults.value = null
    }
}