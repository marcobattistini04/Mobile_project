package com.example.snaphunt.data.repositories


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.snaphunt.data.models.ColorPalette
import com.example.snaphunt.data.models.AppTheme
import kotlinx.coroutines.flow.map

class ThemeRepository(private val dataStore: DataStore<Preferences>) {
    companion object {
        private val THEME_KEY = stringPreferencesKey("theme")
        private val DYNAMIC_COLOR = booleanPreferencesKey("DYNAMIC_COLOR")

        private val PALETTE_KEY = stringPreferencesKey("palette")
    }

    val theme = dataStore.data.map {preferences ->
        try {
             AppTheme.valueOf(preferences[THEME_KEY] ?: "System")
        }catch(_: Exception) {
            AppTheme.System
        }
    }

    suspend fun setTheme(theme: AppTheme) {
        dataStore.edit {preferences -> preferences[THEME_KEY] = theme.toString()}
    }

    val dynamicColor = dataStore.data.map { preferences -> preferences[DYNAMIC_COLOR] ?: true }

    suspend fun setDynamicColor(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[DYNAMIC_COLOR] = enabled }
    }


    val palette = dataStore.data.map {preferences ->
        try {
            ColorPalette.valueOf(preferences[PALETTE_KEY] ?: "Default")
        } catch (_: Exception) {
            ColorPalette.Default
        }
    }

    suspend fun setColorPalette(palette: ColorPalette) {
        dataStore.edit {  preferences -> preferences[PALETTE_KEY] = palette.name}
    }

}

