package com.vkasurinen.notemark.settings.presentation

import com.vkasurinen.notemark.settings.presentation.components.SyncInterval

sealed interface SettingsAction {
    data object NavigateBack : SettingsAction
    data object Logout : SettingsAction
    data object SyncNotes : SettingsAction
    data class SelectSyncInterval(val interval: SyncInterval) : SettingsAction
}