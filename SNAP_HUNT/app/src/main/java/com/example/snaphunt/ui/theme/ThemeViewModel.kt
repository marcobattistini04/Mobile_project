package com.example.snaphunt.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snaphunt.data.models.ColorPalette
import com.example.snaphunt.data.models.AppTheme
import com.example.snaphunt.data.repositories.ThemeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ThemeState(
    val theme: AppTheme,
    val palette: ColorPalette,
    val dynamicColor: Boolean
)

data class ThemeActions(
    val setTheme: (AppTheme) -> Unit,
    val setPalette: (ColorPalette) -> Unit,
    val setDynamicColor: (Boolean) -> Unit
)

class ThemeViewModel(repository: ThemeRepository) : ViewModel() {
    val state = combine(repository.theme, repository.palette, repository.dynamicColor) { theme, palette, dynamicColor ->
        ThemeState(theme, palette, dynamicColor)
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(),
            initialValue = ThemeState(AppTheme.System, ColorPalette.Default, true)
        )
    val actions = ThemeActions(
        setTheme = { theme -> viewModelScope.launch { repository.setTheme(theme) }},
        setPalette = {palette -> viewModelScope.launch { repository.setColorPalette(palette) }},
        setDynamicColor = { dynamicColor -> viewModelScope.launch { repository.setDynamicColor(dynamicColor) }}
    )
}