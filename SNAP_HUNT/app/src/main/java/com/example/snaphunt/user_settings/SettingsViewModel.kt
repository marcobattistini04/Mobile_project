package com.example.snaphunt.user_settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snaphunt.data.models.ColorPalette
import com.example.snaphunt.data.models.AppTheme
import com.example.snaphunt.data.repositories.user_settings.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
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

    private val _syncStatus = MutableStateFlow<SyncResult>(SyncResult.Idle)
    val syncStatus = _syncStatus.asStateFlow()

    sealed class SyncResult {
        object Idle : SyncResult()
        object Loading : SyncResult()
        object Success : SyncResult()
        data class Error(val message: String) : SyncResult()
    }
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
            _syncStatus.value = SyncResult.Loading
            val success = repository.syncToCloud()
            if (success) {
                _syncStatus.value = SyncResult.Success
            } else {
                _syncStatus.value = SyncResult.Error("Unable to synchronize")
            }
        }
    }
}