package com.example.snaphunt.photos // o il pacchetto dove si trova

data class PendingAttempt(
    val id: String,
    val challengeId: String,
    val challengeText: String,
    val localThumbnailPath: String? = null,
    val createdAt: Long,
    val success: Boolean = false,
    val skipped: Boolean = false,
    val aiLabel: String? = null,
    val aiConfidence: Double? = null,
    val points: Int,
    val additionalObjects: Int
)