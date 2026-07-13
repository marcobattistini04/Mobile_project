package com.example.snaphunt.photos

import com.example.snaphunt.network.NetworkMonitor
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.io.File

class SyncManager(
    private val storage: Storage,
    private val db: Postgrest,
    private val networkMonitor: NetworkMonitor
) {

    suspend fun syncAttempt(
        attempt: PendingAttempt,
        userId: String
    ): Boolean {
        if (!networkMonitor.isOnline.value) return false

        return try {

            var storagePath: String? = null

            if(attempt.localThumbnailPath != null) {
                val file = File(attempt.localThumbnailPath)
                if (!file.exists()) return false

                storagePath = "users/$userId/thumbs/${attempt.id}.jpg"

                storage.from("challenge-images").upload(
                    path = storagePath,
                    data = file.readBytes()
                ) {
                    upsert = true
                }
            }



            db.from("challenge_results").insert(
                buildJsonObject {
                    put("id", attempt.id)
                    put("user_id", userId)
                    put("challenge_id", attempt.challengeId)
                    put("challenge_text", attempt.challengeText)
                    put("storage_path", storagePath)
                    put("ai_label", attempt.aiLabel)
                    put("ai_confidence", attempt.aiConfidence)
                    put("success", attempt.success)
                    put("skipped", attempt.skipped)
                    put("points", attempt.points)
                    put("additional_objects", attempt.additionalObjects)
                }
            )

            true

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}