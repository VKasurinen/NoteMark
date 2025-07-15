package com.vkasurinen.notemark.settings.presentation

data class SettingsState(
    val isLoggingOut: Boolean = false,
    val lastSync : Int = 0,
    val syncInterval: String = "Manual only",
    val isSyncing: Boolean = false
)
