package com.example.snaphunt.data.user

import com.example.snaphunt.ui.theme.ThemeState

data class UserLogInData(
    val userId: String,
    val username: String?,
    val profilePictureUri: String?
)

data class UserSettings(
    val themeState: ThemeState,
    val notificationEnabled: Boolean
)