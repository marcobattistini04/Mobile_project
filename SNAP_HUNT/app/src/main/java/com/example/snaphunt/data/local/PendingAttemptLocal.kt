package com.example.snaphunt.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.snaphunt.photos.PendingAttempt

@Entity(tableName = "pending_attempts")
data class PendingAttemptEntity(
    @PrimaryKey val id: String,          // ID del tentativo (UUID generato nello scatto)
    val challengeId: String,             // ID della sfida
    val challengeText: String,           // Il testo della sfida (serve a Supabase/AI)
    val localImagePath: String,          // Il percorso sul telefono (per ritrovare la foto)
    val localThumbnailPath: String,       //percorso della miniatura leggera da caricare su supabase
    val createdAt: Long,                 // Data dello scatto
    val synced: Boolean = false,         // Flag di controllo per il sync Room

    val aiLabel: String? = null,
    val aiConfidence: Double? = null,
    val success: Boolean = false         // Di default false, diventerà true se la sfida è superata
)

// Da Entity (Database) a Domain (App/UI)
fun PendingAttemptEntity.toDomain(): PendingAttempt {
    return PendingAttempt(
        id = this.id,
        challengeId = this.challengeId,
        challengeText = this.challengeText,
        localImagePath = this.localImagePath,
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
        localImagePath = this.localImagePath,
        localThumbnailPath = this.localThumbnailPath,
        createdAt = this.createdAt,
        synced = false, // Di default la nuova entità per il db room non è ancora sincronizzata
        success = this.success,
        aiLabel = this.aiLabel,
        aiConfidence = this.aiConfidence
    )
}