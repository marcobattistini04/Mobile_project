package com.example.snaphunt.data.repositories.user_settings

import com.example.snaphunt.data.models.AppTheme
import com.example.snaphunt.data.models.ColorPalette
import com.example.snaphunt.data.user.UserSettings
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SettingsCloudRepository(
    private val firestore: FirebaseFirestore
) {

    suspend fun upload(userId: String, settings: UserSettings) {
        firestore.collection("users")
            .document(userId)
            .collection("settings")
            .document("main")
            .set(settings)
            .await()
    }

    suspend fun download(userId: String): UserSettings? {
        val doc = firestore.collection("users")
            .document(userId)
            .collection("settings")
            .document("main")
            .get()
            .await()

        if (!doc.exists()) return null

        return UserSettings(
            notificationEnabled = doc.getBoolean("notificationEnabled") ?: true,
            theme = AppTheme.valueOf(doc.getString("theme") ?: "System"),
            dynamicColor = doc.getBoolean("dynamicColor") ?: true,
            palette = ColorPalette.valueOf(doc.getString("palette") ?: "Default"),
            lastUpdated = doc.getLong("lastUpdated") ?: 0L
        )
    }
}