package com.example.snaphunt.user_settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snaphunt.data.models.ColorPalette
import com.example.snaphunt.data.models.AppTheme
import com.example.snaphunt.data.repositories.SettingsCloudRepository
import com.example.snaphunt.data.repositories.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsState(
    val notification: Boolean,
    val theme: AppTheme,
    val palette: ColorPalette,
    val dynamicColor: Boolean
)

data class SettingsActions(
    val setEnableNotifications: (Boolean) -> Unit,
    val setTheme: (AppTheme) -> Unit,
    val setPalette: (ColorPalette) -> Unit,
    val setDynamicColor: (Boolean) -> Unit
)

class SettingsViewModel(repo: SettingsRepository) : ViewModel() {
    private val repository = repo
    val state = combine(repository.notification, repository.theme, repository.palette, repository.dynamicColor) {notification, theme, palette, dynamicColor ->
        SettingsState(notification, theme, palette, dynamicColor)
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(),
            initialValue = SettingsState(false, AppTheme.System, ColorPalette.Default, true)
        )
    val actions = SettingsActions(
        setEnableNotifications = {notification -> viewModelScope.launch { repository.setNotificationEnabled(notification) }},
        setTheme = { theme -> viewModelScope.launch { repository.setTheme(theme) }},
        setPalette = {palette -> viewModelScope.launch { repository.setColorPalette(palette) }},
        setDynamicColor = { dynamicColor -> viewModelScope.launch { repository.setDynamicColor(dynamicColor) }}
    )

    fun demandSyncToCloud() {
        viewModelScope.launch {
            repository.syncToCloud()
        }
    }
}