package com.example.snaphunt.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.snaphunt.photos.PendingAttempt

@Entity(tableName = "pending_attempts")
data class PendingAttemptEntity(
    @PrimaryKey val id: String,          // ID of the attempt (generated)
    val challengeId: String,             // ID of the challenge
    val challengeText: String,           // the text of the challenge
    val localThumbnailPath: String? = null,       //path of the thumbnail to upload on Supabase
    val createdAt: Long,                 // date of the photo
    val synced: Boolean = false,         // control flag for Room sync
    val aiLabel: String? = null,
    val aiConfidence: Double? = null,
    val success: Boolean = false,         // default on false, true if the challenge is succeded
    val skipped: Boolean = false,
    val points: Int = 0,
    val additionalObjects: Int = 0
)

// from Entity (Database) to Domain (App/UI)
fun PendingAttemptEntity.toDomain(): PendingAttempt {
    return PendingAttempt(
        id = this.id,
        challengeId = this.challengeId,
        challengeText = this.challengeText,
        localThumbnailPath = this.localThumbnailPath,
        createdAt = this.createdAt,
        success = this.success,
        skipped = this.skipped,
        aiLabel = this.aiLabel,
        aiConfidence = this.aiConfidence,
        points = this.points,
        additionalObjects = this.additionalObjects
    )
}

// from Domain (App/UI) to Entity (Database)
fun PendingAttempt.toEntity(): PendingAttemptEntity {
    return PendingAttemptEntity(
        id = this.id,
        challengeId = this.challengeId,
        challengeText = this.challengeText,
        localThumbnailPath = this.localThumbnailPath,
        createdAt = this.createdAt,
        synced = false,
        success = this.success,
        skipped = this.skipped,
        aiLabel = this.aiLabel,
        aiConfidence = this.aiConfidence,
        points = this.points,
        additionalObjects = this.additionalObjects
    )
}