package com.vkasurinen.notemark.settings.presentation

import com.vkasurinen.notemark.settings.presentation.components.SyncInterval

data class SettingsState(
    val isLoggingOut: Boolean = false,
    val lastSync : String? = null,
    val isSyncing: Boolean = false,
    val syncInterval: SyncInterval = SyncInterval.MANUAL
)
