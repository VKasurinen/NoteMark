package com.vkasurinen.notemark.settings.presentation

sealed interface SettingsAction {
    data object NavigateBack : SettingsAction
    data object Logout : SettingsAction
    data class SelectSyncInterval(val interval: String) : SettingsAction
    data object SyncNotes : SettingsAction
}