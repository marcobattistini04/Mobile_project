package com.example.snaphunt.data.user

import com.example.snaphunt.data.models.AppTheme
import com.example.snaphunt.data.models.ColorPalette

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