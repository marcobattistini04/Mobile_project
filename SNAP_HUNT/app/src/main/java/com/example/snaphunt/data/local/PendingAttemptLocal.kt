package com.example.snaphunt.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.snaphunt.photos.PendingAttempt

@Entity(tableName = "pending_attempts")
data class PendingAttemptEntity(
    @PrimaryKey val id: String,          // ID of the attempt (generated)
    val challengeId: String,             // ID of the challenge
    val challengeText: String,           // the text of the challenge
    val localThumbnailPath: String,       //path of the thumbnail to upload on Supabase
    val createdAt: Long,                 // date of the photo
    val synced: Boolean = false,         // control flag for Room sync
    val aiLabel: String? = null,
    val aiConfidence: Double? = null,
    val success: Boolean = false         // default on false, true if the challenge is succeded
)

// Da Entity (Database) a Domain (App/UI)
fun PendingAttemptEntity.toDomain(): PendingAttempt {
    return PendingAttempt(
        id = this.id,
        challengeId = this.challengeId,
        challengeText = this.challengeText,
        localThumbnailPath = this.localThumbnailPath,
        createdAt = this.createdAt,
        success = this.success,
        aiLabel = this.aiLabel,
        aiConfidence = this.aiConfidence
    )
}

// Da Domain (App/UI) a Entity (Database)
fun PendingAttempt.toEntity(): PendingAttemptEntity {
    return PendingAttemptEntity(
        id = this.id,
        challengeId = this.challengeId,
        challengeText = this.challengeText,
        localThumbnailPath = this.localThumbnailPath,
        createdAt = this.createdAt,
        synced = false,
        success = this.success,
        aiLabel = this.aiLabel,
        aiConfidence = this.aiConfidence
    )
}