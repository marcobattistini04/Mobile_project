package com.example.snaphunt.data.user

import com.example.snaphunt.data.models.AppTheme
import com.example.snaphunt.data.models.ColorPalette
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class UserLogInData(
    val userId: String,
    val username: String?,
    val profilePictureUri: String?
)

data class UserSettings(
    val notificationEnabled: Boolean = true,
    val theme: AppTheme = AppTheme.System,
    val dynamicColor: Boolean = true,
    val palette: ColorPalette = ColorPalette.Default,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    constructor() : this(true, AppTheme.System, true, ColorPalette.Default, 0L)
}

@Serializable
data class UserChallengeItem(
    @SerialName("id")
    val id: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("challenge_text")
    val challengeText: String,
    @SerialName("storage_path")
    val storagePath: String? = null,
    @SerialName("ai_label")
    val aiLabel: String? = null,
    @SerialName("ai_confidence")
    val aiConfidence: Float? = null,
    @SerialName("success")
    val success: Boolean,
    @SerialName("skipped")
    val skipped: Boolean,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("challenge_id")
    val challengeId: String,
    @SerialName("points")
    val points: Int,
    @SerialName("additional_objects")
    val additionalObjects: Int
)