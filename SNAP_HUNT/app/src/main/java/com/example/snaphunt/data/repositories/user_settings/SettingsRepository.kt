package com.example.snaphunt.data.repositories.user_settings


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.snaphunt.data.models.ColorPalette
import com.example.snaphunt.data.models.AppTheme
import com.example.snaphunt.data.user.UserSettings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.SupabaseClient

class SettingsRepository(
    private val dataStore: DataStore<Preferences>,
    private val cloud: SettingsCloudRepository,
    private val supabase: SupabaseClient
) {
    companion object {

        private val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
        private val THEME_KEY = stringPreferencesKey("theme")
        private val DYNAMIC_COLOR = booleanPreferencesKey("DYNAMIC_COLOR")

        private val PALETTE_KEY = stringPreferencesKey("palette")

        private val LAST_UPDATED = longPreferencesKey("last_updated")
    }

    val notification = dataStore.data.map {preferences ->  preferences[NOTIFICATION_ENABLED] ?: true}

    suspend fun setNotificationEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATION_ENABLED] = enabled
            preferences[LAST_UPDATED] = System.currentTimeMillis()
        }
    }

    val theme = dataStore.data.map {preferences ->
        try {
             AppTheme.valueOf(preferences[THEME_KEY] ?: "System")
        }catch(_: Exception) {
            AppTheme.System
        }
    }

    suspend fun setTheme(theme: AppTheme) {
        dataStore.edit {preferences ->
            preferences[THEME_KEY] = theme.toString()
            preferences[LAST_UPDATED] = System.currentTimeMillis()
        }
    }

    val dynamicColor = dataStore.data.map { preferences -> preferences[DYNAMIC_COLOR] ?: true }

    suspend fun setDynamicColor(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DYNAMIC_COLOR] = enabled
            preferences[LAST_UPDATED] = System.currentTimeMillis()
        }
    }

    val palette = dataStore.data.map {preferences ->
        try {
            ColorPalette.valueOf(preferences[PALETTE_KEY] ?: "Default")
        } catch (_: Exception) {
            ColorPalette.Default
        }
    }

    suspend fun setColorPalette(palette: ColorPalette) {
        dataStore.edit {
            preferences -> preferences[PALETTE_KEY] = palette.name
            preferences[LAST_UPDATED] = System.currentTimeMillis()
        }
    }

    suspend fun getCurrentSettings(): UserSettings {
        val prefs = dataStore.data.first()

        return UserSettings(
            notificationEnabled = prefs[NOTIFICATION_ENABLED] ?: true,
            theme = try {
                AppTheme.valueOf(prefs[THEME_KEY] ?: "System")
            } catch (_: Exception) {
                AppTheme.System
            },
            dynamicColor = prefs[DYNAMIC_COLOR] ?: true,
            palette = try {
                ColorPalette.valueOf(prefs[PALETTE_KEY] ?: "Default")
            } catch (_: Exception) {
                ColorPalette.Default
            },
            lastUpdated = prefs[LAST_UPDATED] ?: 0L
        )
    }

    suspend fun syncToCloud() {
        val userId = supabase.auth.sessionManager.loadSession()?.user?.id  ?: return
        val settings = getCurrentSettings()

        cloud.upload(userId, settings)
    }

    suspend fun syncFromCloud() {
        val userId = supabase.auth.sessionManager.loadSession()?.user?.id ?: return
        val cloudSettings = cloud.download(userId) ?: return

        dataStore.edit { prefs ->
            prefs[THEME_KEY] = cloudSettings.theme.name
            prefs[PALETTE_KEY] = cloudSettings.palette.name
            prefs[NOTIFICATION_ENABLED] = cloudSettings.notificationEnabled
            prefs[DYNAMIC_COLOR] = cloudSettings.dynamicColor
            prefs[LAST_UPDATED] = cloudSettings.lastUpdated
        }
    }
}

