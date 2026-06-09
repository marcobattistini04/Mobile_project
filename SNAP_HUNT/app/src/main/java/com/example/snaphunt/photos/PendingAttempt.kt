package com.example.snaphunt.photos // o il pacchetto dove si trova

data class PendingAttempt(
    val id: String,
    val challengeId: String,
    val challengeText: String,
    val localThumbnailPath: String,
    val createdAt: Long,
    val success: Boolean,
    val aiLabel: String? = null,
    val aiConfidence: Double? = null
)