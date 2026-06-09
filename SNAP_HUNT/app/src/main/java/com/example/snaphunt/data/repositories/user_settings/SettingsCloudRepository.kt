package com.example.snaphunt.data.repositories.user_settings


import com.example.snaphunt.data.models.AppTheme
import com.example.snaphunt.data.models.ColorPalette
import com.example.snaphunt.data.user.UserSettings
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable

@Serializable
data class UserSettingsRow(
    val user_id: String,
    val notification_enabled: Boolean,
    val theme: String,
    val dynamic_color: Boolean,
    val palette: String,
    val last_updated: Long
)

class SettingsCloudRepository(
    private val supabase: SupabaseClient
) {

    suspend fun upload(userId: String, settings: UserSettings): Boolean {
        return try {
            val row = UserSettingsRow(
                user_id = userId,
                notification_enabled = settings.notificationEnabled,
                theme = settings.theme.name,
                dynamic_color = settings.dynamicColor,
                palette = settings.palette.name,
                last_updated = settings.lastUpdated
            )

            supabase.from("user_settings").upsert(row)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun download(userId: String): UserSettings? {
        return try {
            val row = supabase.from("user_settings")
                .select{ filter {eq("user_id", userId)  } }
                .decodeSingleOrNull<UserSettingsRow>()
                ?: return null

            UserSettings(
                notificationEnabled = row.notification_enabled,
                theme = AppTheme.valueOf(row.theme),
                dynamicColor = row.dynamic_color,
                palette = ColorPalette.valueOf(row.palette),
                lastUpdated = row.last_updated
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}